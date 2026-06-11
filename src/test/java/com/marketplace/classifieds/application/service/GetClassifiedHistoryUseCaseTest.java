package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.adapter.in.web.dto.response.StatusHistoryResponse;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.exception.ClassifiedNotFoundException;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GetClassifiedHistoryUseCaseTest {

    private ClassifiedPort classifiedPort;
    private ClassifiedStatusHistoryPort historyPort;
    private ClassifiedValidationService validationService;
    private ClassifiedStatusService statusService;

    private ClassifiedService service;

    @BeforeEach
    void setup() {
        classifiedPort = mock(ClassifiedPort.class);
        historyPort = mock(ClassifiedStatusHistoryPort.class);
        validationService = mock(ClassifiedValidationService.class);
        statusService = mock(ClassifiedStatusService.class);

        service = new ClassifiedService(
                classifiedPort, historyPort, validationService, statusService
        );
    }

    @Test
    void getStatusHistory_shouldReturnMappedHistory() {
        Long id = 77L;
        LocalDateTime now = LocalDateTime.now();

        Classified entity = Classified.builder()
                .id(id)
                .title("Ev")
                .description("Açıklama")
                .category(ClassifiedCategory.EMLAK)
                .status(ClassifiedStatus.DEAKTIF)
                .createdBy("system")
                .createdAt(now)
                .endDate(now.plusWeeks(4))
                .build();

        when(classifiedPort.findById(id)).thenReturn(Optional.of(entity));

        ClassifiedStatusHistory h1 = ClassifiedStatusHistory.builder()
                .id(1L)
                .previousStatus(ClassifiedStatus.ONAY_BEKLIYOR)
                .newStatus(ClassifiedStatus.AKTIF)
                .changedAt(now.minusDays(1))
                .changedBy("admin")
                .reason("Onaylandı")
                .build();

        ClassifiedStatusHistory h2 = ClassifiedStatusHistory.builder()
                .id(2L)
                .previousStatus(ClassifiedStatus.AKTIF)
                .newStatus(ClassifiedStatus.DEAKTIF)
                .changedAt(now.minusHours(1))
                .changedBy("system")
                .reason("Süre doldu")
                .build();

        when(historyPort.findByClassifiedIdOrderByChangedAtDesc(id))
                .thenReturn(List.of(h2, h1));

        List<StatusHistoryResponse> result = service.getStatusHistory(id);

        assertEquals(2, result.size());

        StatusHistoryResponse first = result.get(0);
        assertEquals(2L, first.getId());
        assertEquals(ClassifiedStatus.AKTIF, first.getPreviousStatus());
        assertEquals(ClassifiedStatus.DEAKTIF, first.getNewStatus());
        assertEquals("system", first.getChangedBy());
        assertEquals("Süre doldu", first.getReason());

        StatusHistoryResponse second = result.get(1);
        assertEquals(1L, second.getId());
        assertEquals(ClassifiedStatus.ONAY_BEKLIYOR, second.getPreviousStatus());
        assertEquals(ClassifiedStatus.AKTIF, second.getNewStatus());
    }

    @Test
    void getStatusHistory_shouldThrowException_whenClassifiedNotFound() {
        Long id = 123L;

        when(classifiedPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClassifiedNotFoundException.class,
                () -> service.getStatusHistory(id));

        verify(historyPort, never())
                .findByClassifiedIdOrderByChangedAtDesc(anyLong());
    }
}

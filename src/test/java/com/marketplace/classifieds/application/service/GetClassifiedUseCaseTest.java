package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.domain.exception.ClassifiedNotFoundException;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GetClassifiedUseCaseTest {

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
                classifiedPort,
                historyPort,
                validationService,
                statusService
        );
    }

    @Test
    void get_shouldReturnClassifiedResponse_whenFound() {
        Long id = 100L;
        LocalDateTime now = LocalDateTime.now();

        Classified entity = Classified.builder()
                .id(id)
                .title("Ev Satılık")
                .description("Denize yakın")
                .category(ClassifiedCategory.EMLAK)
                .status(ClassifiedStatus.AKTIF)
                .createdBy("system")
                .createdAt(now)
                .endDate(now.plusWeeks(4))
                .build();

        when(classifiedPort.findById(id))
                .thenReturn(Optional.of(entity));

        var response = service.get(id);

        verify(classifiedPort).findById(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Ev Satılık", response.getTitle());
        assertEquals(ClassifiedStatus.AKTIF, response.getStatus());
        assertEquals(ClassifiedCategory.EMLAK, response.getCategory());
        assertEquals(now.plusWeeks(4), response.getEndDate());
    }

    @Test
    void get_shouldThrowException_whenNotFound() {
        Long id = 999L;

        when(classifiedPort.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClassifiedNotFoundException.class, () -> service.get(id));
        verify(classifiedPort).findById(id);
    }
}

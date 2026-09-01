package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.domain.command.UpdateClassifiedStatusCommand;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.exception.ClassifiedNotFoundException;
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

class UpdateClassifiedStatusUseCaseTest {

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
    void updateStatus_shouldUpdateStatus_andReturnResponse() {
        Long id = 10L;
        LocalDateTime now = LocalDateTime.now();

        Classified entity = Classified.builder()
                .id(id)
                .title("Araba")
                .description("Açıklama")
                .category(ClassifiedCategory.VASITA)
                .status(ClassifiedStatus.AKTIF)
                .createdBy("system")
                .createdAt(now)
                .endDate(now.plusWeeks(3))
                .build();

        UpdateClassifiedStatusCommand req =
                new UpdateClassifiedStatusCommand(ClassifiedStatus.DEAKTIF, "Sahibi değişti", "admin");

        when(classifiedPort.findById(id)).thenReturn(Optional.of(entity));
        when(classifiedPort.save(entity)).thenReturn(entity);

        doAnswer(invocation -> {
            Classified c = invocation.getArgument(0);
            ClassifiedStatus newStatus = invocation.getArgument(1);
            c.setStatus(newStatus);
            return null;
        }).when(statusService).changeStatus(any(), any(), anyString(), anyString());

        Classified response = service.updateStatus(id, req);

        verify(classifiedPort).save(entity);

        assertEquals(ClassifiedStatus.DEAKTIF, response.getStatus());
    }


    @Test
    void updateStatus_shouldThrowException_whenNotFound() {
        Long id = 999L;
        UpdateClassifiedStatusCommand req =
                new UpdateClassifiedStatusCommand(ClassifiedStatus.DEAKTIF, "Sebep", "admin");

        when(classifiedPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClassifiedNotFoundException.class,
                () -> service.updateStatus(id, req));

        verify(classifiedPort).findById(id);
        verify(statusService, never()).changeStatus(any(), any(), anyString(), anyString());
        verify(classifiedPort, never()).save(any());
    }

    @Test
    void updateStatus_shouldPropagateDomainException() {
        Long id = 55L;
        LocalDateTime now = LocalDateTime.now();

        Classified entity = Classified.builder()
                .id(id)
                .title("Ev")
                .description("Açıklama")
                .category(ClassifiedCategory.EMLAK)
                .status(ClassifiedStatus.ONAY_BEKLIYOR)
                .createdBy("system")
                .createdAt(now)
                .endDate(now.plusWeeks(4))
                .build();

        UpdateClassifiedStatusCommand req =
                new UpdateClassifiedStatusCommand(ClassifiedStatus.AKTIF, "Tüm şartlar sağlandı", "admin");

        when(classifiedPort.findById(id)).thenReturn(Optional.of(entity));

        doThrow(new RuntimeException("Status geçiş hatası"))
                .when(statusService)
                .changeStatus(any(), any(), anyString(), anyString());

        assertThrows(RuntimeException.class, () -> service.updateStatus(id, req));

        verify(classifiedPort, never()).save(any());
    }
}

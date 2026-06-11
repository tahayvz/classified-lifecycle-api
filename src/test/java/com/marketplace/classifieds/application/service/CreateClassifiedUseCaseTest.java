package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.adapter.in.web.dto.request.CreateClassifiedRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.exception.BadWordException;
import com.marketplace.classifieds.domain.exception.DuplicateClassifiedException;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateClassifiedUseCaseTest {

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
    void create_shouldSaveClassifiedCorrectly_andReturnResponse() {
        CreateClassifiedRequest request = new CreateClassifiedRequest(
                "Temiz Ev",
                "Sıfır bina",
                ClassifiedCategory.EMLAK
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedEndDate = now.plusWeeks(ClassifiedCategory.EMLAK.getDurationInWeeks());

        Classified saved = Classified.builder()
                .id(10L)
                .title("Temiz Ev")
                .description("Sıfır bina")
                .category(ClassifiedCategory.EMLAK)
                .status(ClassifiedStatus.ONAY_BEKLIYOR)
                .createdBy("system")
                .createdAt(now)
                .endDate(expectedEndDate)
                .build();

        when(classifiedPort.save(any(Classified.class)))
                .thenReturn(saved);

        ClassifiedResponse response = service.create(request);

        verify(validationService).validateBadWords("Temiz Ev", "Sıfır bina");
        verify(validationService).validateDuplicate("Temiz Ev", "Sıfır bina", ClassifiedCategory.EMLAK);
        verify(classifiedPort).save(any(Classified.class));

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Temiz Ev", response.getTitle());
        assertEquals(ClassifiedStatus.ONAY_BEKLIYOR, response.getStatus());
        assertEquals(expectedEndDate, response.getEndDate());
    }

    @Test
    void create_shouldFail_whenBadWordsFound() {
        CreateClassifiedRequest req = new CreateClassifiedRequest(
                "opsiyonlu",
                "mpi İçerik",
                ClassifiedCategory.DIGER
        );

        doThrow(new BadWordException("Yasaklı kelime"))
                .when(validationService)
                .validateBadWords(anyString(), anyString());

        assertThrows(BadWordException.class, () -> service.create(req));
        verify(classifiedPort, never()).save(any());
    }

    @Test
    void create_shouldFail_whenDuplicateExists() {
        CreateClassifiedRequest req = new CreateClassifiedRequest(
                "Başlık",
                "Açıklama",
                ClassifiedCategory.ALISVERIS
        );

        doNothing().when(validationService).validateBadWords(anyString(), anyString());

        doThrow(new DuplicateClassifiedException("Duplicate ilan"))
                .when(validationService)
                .validateDuplicate(anyString(), anyString(), any());

        assertThrows(DuplicateClassifiedException.class, () -> service.create(req));
        verify(classifiedPort, never()).save(any());
    }
}

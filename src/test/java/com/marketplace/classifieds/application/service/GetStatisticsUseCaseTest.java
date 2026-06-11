package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedStatisticsResponse;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GetStatisticsUseCaseTest {

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
    void getStatistics_shouldReturnCorrectStatistics() {
        List<Object[]> grouped = List.of(
                new Object[]{"AKTIF", 5L},
                new Object[]{"DEAKTIF", 3L},
                new Object[]{"ONAY_BEKLIYOR", 2L}
        );

        when(classifiedPort.countByStatusGrouped()).thenReturn(grouped);

        var response = service.getStatistics();

        verify(classifiedPort).countByStatusGrouped();

        assertEquals(5L, response.getStatistics().get("AKTIF"));
        assertEquals(3L, response.getStatistics().get("DEAKTIF"));
        assertEquals(2L, response.getStatistics().get("ONAY_BEKLIYOR"));

        assertEquals(10L, response.getTotalClassifieds());
    }

    @Test
    void getStatistics_shouldReturnZero_whenEmptyList() {
        when(classifiedPort.countByStatusGrouped()).thenReturn(List.of());

        ClassifiedStatisticsResponse response = service.getStatistics();

        verify(classifiedPort).countByStatusGrouped();

        assertNotNull(response);
        assertTrue(response.getStatistics().isEmpty());
        assertEquals(0L, response.getTotalClassifieds());
    }

    @Test
    void getStatistics_shouldHandleNullStatus_asUnknown() {
        List<Object[]> grouped = List.of(
                new Object[]{null, 4L},
                new Object[]{"AKTIF", 2L}
        );

        when(classifiedPort.countByStatusGrouped()).thenReturn(grouped);

        var response = service.getStatistics();

        verify(classifiedPort).countByStatusGrouped();

        assertEquals(4L, response.getStatistics().get("UNKNOWN"));
        assertEquals(2L, response.getStatistics().get("AKTIF"));
        assertEquals(6L, response.getTotalClassifieds());
    }

    @Test
    void getStatistics_shouldHandleNullCount_asZero() {
        List<Object[]> grouped = List.of(
                new Object[]{"AKTIF", null},
                new Object[]{"DEAKTIF", 3L}
        );

        when(classifiedPort.countByStatusGrouped()).thenReturn(grouped);

        var response = service.getStatistics();

        verify(classifiedPort).countByStatusGrouped();

        assertEquals(0L, response.getStatistics().get("AKTIF"));
        assertEquals(3L, response.getStatistics().get("DEAKTIF"));
        assertEquals(3L, response.getTotalClassifieds());
    }

}

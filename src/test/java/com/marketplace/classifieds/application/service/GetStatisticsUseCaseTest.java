package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.model.ClassifiedStatistics;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetStatisticsUseCaseTest {

    private ClassifiedPort classifiedPort;
    private ClassifiedService service;

    @BeforeEach
    void setup() {
        classifiedPort = mock(ClassifiedPort.class);

        service = new ClassifiedService(
                classifiedPort,
                mock(ClassifiedStatusHistoryPort.class),
                mock(ClassifiedValidationService.class),
                mock(ClassifiedStatusService.class)
        );
    }

    private Map<ClassifiedStatus, Long> counts(Object... pairs) {
        Map<ClassifiedStatus, Long> map = new EnumMap<>(ClassifiedStatus.class);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((ClassifiedStatus) pairs[i], (Long) pairs[i + 1]);
        }
        return map;
    }

    @Test
    void getStatistics_shouldReportCountPerStatus() {
        when(classifiedPort.countByStatus()).thenReturn(counts(
                ClassifiedStatus.AKTIF, 5L,
                ClassifiedStatus.DEAKTIF, 3L,
                ClassifiedStatus.ONAY_BEKLIYOR, 2L));

        ClassifiedStatistics statistics = service.getStatistics();

        verify(classifiedPort).countByStatus();
        assertThat(statistics.countByStatus())
                .containsEntry(ClassifiedStatus.AKTIF, 5L)
                .containsEntry(ClassifiedStatus.DEAKTIF, 3L)
                .containsEntry(ClassifiedStatus.ONAY_BEKLIYOR, 2L);
    }

    @Test
    void getStatistics_shouldSumEveryStatusIntoTheTotal() {
        when(classifiedPort.countByStatus()).thenReturn(counts(
                ClassifiedStatus.AKTIF, 5L,
                ClassifiedStatus.DEAKTIF, 3L,
                ClassifiedStatus.ONAY_BEKLIYOR, 2L));

        assertThat(service.getStatistics().total()).isEqualTo(10L);
    }

    @Test
    void getStatistics_shouldReturnEmptyStatistics_whenNothingIsStored() {
        when(classifiedPort.countByStatus()).thenReturn(Map.of());

        ClassifiedStatistics statistics = service.getStatistics();

        assertThat(statistics.countByStatus()).isEmpty();
        assertThat(statistics.total()).isZero();
    }

    @Test
    void getStatistics_shouldOnlyCountStatusesThatArePresent() {
        when(classifiedPort.countByStatus()).thenReturn(counts(ClassifiedStatus.AKTIF, 7L));

        ClassifiedStatistics statistics = service.getStatistics();

        assertThat(statistics.countByStatus()).hasSize(1);
        assertThat(statistics.total()).isEqualTo(7L);
    }

    @Test
    void statistics_shouldBeImmutable() {
        when(classifiedPort.countByStatus()).thenReturn(counts(ClassifiedStatus.AKTIF, 1L));

        Map<ClassifiedStatus, Long> exposed = service.getStatistics().countByStatus();

        assertThatThrownBy(() -> exposed.put(ClassifiedStatus.DEAKTIF, 9L))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}

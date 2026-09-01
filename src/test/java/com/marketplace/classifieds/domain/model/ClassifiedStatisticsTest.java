package com.marketplace.classifieds.domain.model;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassifiedStatisticsTest {

    @Test
    void of_shouldSumCountsIntoTotal() {
        ClassifiedStatistics statistics = ClassifiedStatistics.of(Map.of(
                ClassifiedStatus.AKTIF, 4L,
                ClassifiedStatus.DEAKTIF, 6L));

        assertThat(statistics.total()).isEqualTo(10L);
    }

    @Test
    void of_shouldReturnEmptyStatistics_forEmptyMap() {
        ClassifiedStatistics statistics = ClassifiedStatistics.of(Map.of());

        assertThat(statistics.countByStatus()).isEmpty();
        assertThat(statistics.total()).isZero();
    }

    @Test
    void of_shouldTreatNullAsEmpty() {
        ClassifiedStatistics statistics = ClassifiedStatistics.of(null);

        assertThat(statistics.countByStatus()).isEmpty();
        assertThat(statistics.total()).isZero();
    }

    @Test
    void constructor_shouldTreatNullMapAsEmpty() {
        ClassifiedStatistics statistics = new ClassifiedStatistics(null, 0L);

        assertThat(statistics.countByStatus()).isEmpty();
    }

    @Test
    void of_shouldTreatANullCountAsZero() {
        Map<ClassifiedStatus, Long> source = new HashMap<>();
        source.put(ClassifiedStatus.AKTIF, null);
        source.put(ClassifiedStatus.DEAKTIF, 4L);

        ClassifiedStatistics statistics = ClassifiedStatistics.of(source);

        assertThat(statistics.total()).isEqualTo(4L);
    }

    @Test
    void countByStatus_shouldBeUnmodifiable() {
        ClassifiedStatistics statistics = ClassifiedStatistics.of(Map.of(ClassifiedStatus.AKTIF, 1L));

        assertThatThrownBy(() -> statistics.countByStatus().put(ClassifiedStatus.DEAKTIF, 2L))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void countByStatus_shouldNotReflectLaterChangesToTheSourceMap() {
        Map<ClassifiedStatus, Long> source = new HashMap<>();
        source.put(ClassifiedStatus.AKTIF, 1L);

        ClassifiedStatistics statistics = ClassifiedStatistics.of(source);
        source.put(ClassifiedStatus.DEAKTIF, 99L);

        assertThat(statistics.countByStatus()).hasSize(1);
    }

    @Test
    void of_shouldAcceptAnEnumMapSource() {
        Map<ClassifiedStatus, Long> source = new EnumMap<>(ClassifiedStatus.class);
        source.put(ClassifiedStatus.ONAY_BEKLIYOR, 3L);

        assertThat(ClassifiedStatistics.of(source).total()).isEqualTo(3L);
    }
}

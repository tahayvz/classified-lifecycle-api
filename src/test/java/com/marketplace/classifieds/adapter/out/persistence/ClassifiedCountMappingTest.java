package com.marketplace.classifieds.adapter.out.persistence;

import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedJpaRepository;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Grouped count rows are mapped defensively")
class ClassifiedCountMappingTest {

    private ClassifiedJpaRepository repository;
    private ClassifiedPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ClassifiedJpaRepository.class);
        adapter = new ClassifiedPersistenceAdapter(repository);
    }

    @Test
    void countByStatus_shouldMapTypedRows() {
        when(repository.countByStatusGrouped()).thenReturn(List.of(
                new Object[]{ClassifiedStatus.AKTIF, 4L},
                new Object[]{ClassifiedStatus.DEAKTIF, 1L}));

        assertThat(adapter.countByStatus())
                .containsEntry(ClassifiedStatus.AKTIF, 4L)
                .containsEntry(ClassifiedStatus.DEAKTIF, 1L);
    }

    @Test
    void countByStatus_shouldTreatMissingCountAsZero() {
        when(repository.countByStatusGrouped()).thenReturn(List.<Object[]>of(
                new Object[]{ClassifiedStatus.AKTIF, null}));

        assertThat(adapter.countByStatus()).containsEntry(ClassifiedStatus.AKTIF, 0L);
    }

    @Test
    void countByStatus_shouldNarrowAnyNumericCount() {
        when(repository.countByStatusGrouped()).thenReturn(List.<Object[]>of(
                new Object[]{ClassifiedStatus.AKTIF, Integer.valueOf(7)}));

        assertThat(adapter.countByStatus()).containsEntry(ClassifiedStatus.AKTIF, 7L);
    }

    @Test
    void countByStatus_shouldSkipRowsWithAnUnexpectedKeyType() {
        when(repository.countByStatusGrouped()).thenReturn(List.of(
                new Object[]{"AKTIF", 3L},
                new Object[]{null, 2L},
                new Object[]{ClassifiedStatus.DEAKTIF, 5L}));

        Map<ClassifiedStatus, Long> counts = adapter.countByStatus();

        assertThat(counts).containsExactly(Map.entry(ClassifiedStatus.DEAKTIF, 5L));
    }

    @Test
    void countByStatus_shouldBeEmpty_whenTheQueryReturnsNothing() {
        when(repository.countByStatusGrouped()).thenReturn(List.of());

        assertThat(adapter.countByStatus()).isEmpty();
    }
}

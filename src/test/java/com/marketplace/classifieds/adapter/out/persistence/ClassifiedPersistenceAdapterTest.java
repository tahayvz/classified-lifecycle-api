package com.marketplace.classifieds.adapter.out.persistence;

import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedJpaRepository;
import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedStatusHistoryJpaRepository;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ClassifiedPersistenceAdapter.class, ClassifiedStatusHistoryPersistenceAdapter.class})
class ClassifiedPersistenceAdapterTest {

    @Autowired
    private ClassifiedPersistenceAdapter adapter;

    @Autowired
    private ClassifiedStatusHistoryPersistenceAdapter historyAdapter;

    @Autowired
    private ClassifiedJpaRepository jpaRepository;

    @Autowired
    private ClassifiedStatusHistoryJpaRepository historyJpaRepository;

    @BeforeEach
    void clearDatabase() {
        historyJpaRepository.deleteAll();
        jpaRepository.deleteAll();
    }

    private Classified newClassified(String title, ClassifiedCategory category) {
        return Classified.builder()
                .title(title)
                .description("description of " + title)
                .category(category)
                .createdBy("tester")
                .build();
    }

    @Test
    void save_shouldAssignIdAndApplyPrePersistDefaults() {
        Classified saved = adapter.save(newClassified("daire", ClassifiedCategory.EMLAK));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(ClassifiedStatus.ONAY_BEKLIYOR);
        assertThat(saved.getEndDate()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_shouldReturnPersistedClassified() {
        Classified saved = adapter.save(newClassified("araba", ClassifiedCategory.VASITA));

        Optional<Classified> found = adapter.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("araba");
    }

    @Test
    void findById_shouldReturnEmpty_whenIdIsUnknown() {
        assertThat(adapter.findById(999_999L)).isEmpty();
    }

    @Test
    void existsByTitleAndDescription_shouldMatchOnAllThreeFields() {
        adapter.save(newClassified("kanepe", ClassifiedCategory.ALISVERIS));

        assertThat(adapter.existsByTitleAndDescription(
                "kanepe", "description of kanepe", ClassifiedCategory.ALISVERIS)).isTrue();
    }

    @Test
    void existsByTitleAndDescription_shouldNotMatch_whenCategoryDiffers() {
        adapter.save(newClassified("kanepe", ClassifiedCategory.ALISVERIS));

        assertThat(adapter.existsByTitleAndDescription(
                "kanepe", "description of kanepe", ClassifiedCategory.EMLAK)).isFalse();
    }

    @Test
    void existsByTitleAndDescription_shouldNotMatch_whenDescriptionDiffers() {
        adapter.save(newClassified("kanepe", ClassifiedCategory.ALISVERIS));

        assertThat(adapter.existsByTitleAndDescription(
                "kanepe", "baska aciklama", ClassifiedCategory.ALISVERIS)).isFalse();
    }

    @Test
    void countByStatusGrouped_shouldReturnOneRowPerUsedStatus() {
        adapter.save(newClassified("daire", ClassifiedCategory.EMLAK));
        adapter.save(newClassified("villa", ClassifiedCategory.EMLAK));
        adapter.save(newClassified("kanepe", ClassifiedCategory.ALISVERIS));

        List<Object[]> grouped = adapter.countByStatusGrouped();

        assertThat(grouped).hasSize(2);
        assertThat(grouped)
                .anySatisfy(row -> {
                    assertThat(row[0]).isEqualTo(ClassifiedStatus.ONAY_BEKLIYOR);
                    assertThat(row[1]).isEqualTo(2L);
                })
                .anySatisfy(row -> {
                    assertThat(row[0]).isEqualTo(ClassifiedStatus.AKTIF);
                    assertThat(row[1]).isEqualTo(1L);
                });
    }

    @Test
    void countByStatusGrouped_shouldBeEmpty_whenNoClassifiedExists() {
        assertThat(adapter.countByStatusGrouped()).isEmpty();
    }

    @Test
    void history_shouldBeReturnedNewestFirst() {
        Classified classified = adapter.save(newClassified("daire", ClassifiedCategory.EMLAK));

        historyAdapter.save(ClassifiedStatusHistory.builder()
                .classified(classified)
                .previousStatus(ClassifiedStatus.ONAY_BEKLIYOR)
                .newStatus(ClassifiedStatus.AKTIF)
                .changedBy("admin")
                .reason("approved")
                .build());
        historyAdapter.save(ClassifiedStatusHistory.builder()
                .classified(classified)
                .previousStatus(ClassifiedStatus.AKTIF)
                .newStatus(ClassifiedStatus.DEAKTIF)
                .changedBy("admin")
                .reason("expired")
                .build());

        List<ClassifiedStatusHistory> history =
                historyAdapter.findByClassifiedIdOrderByChangedAtDesc(classified.getId());

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getNewStatus()).isEqualTo(ClassifiedStatus.DEAKTIF);
        assertThat(history.get(1).getNewStatus()).isEqualTo(ClassifiedStatus.AKTIF);
    }

    @Test
    void history_shouldBeEmpty_forClassifiedWithoutTransitions() {
        Classified classified = adapter.save(newClassified("daire", ClassifiedCategory.EMLAK));

        assertThat(historyAdapter.findByClassifiedIdOrderByChangedAtDesc(classified.getId())).isEmpty();
    }
}

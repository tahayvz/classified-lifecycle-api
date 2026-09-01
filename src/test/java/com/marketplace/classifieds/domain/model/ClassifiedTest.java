package com.marketplace.classifieds.domain.model;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClassifiedTest {

    private Classified withCategory(ClassifiedCategory category) {
        return Classified.builder()
                .title("title")
                .description("description")
                .category(category)
                .createdBy("tester")
                .build();
    }

    @ParameterizedTest
    @EnumSource(value = ClassifiedCategory.class, names = {"EMLAK", "VASITA", "DIGER"})
    void prePersist_shouldRequireApproval_forModeratedCategories(ClassifiedCategory category) {
        Classified classified = withCategory(category);

        classified.prePersist();

        assertThat(classified.getStatus()).isEqualTo(ClassifiedStatus.ONAY_BEKLIYOR);
    }

    @Test
    void prePersist_shouldPublishImmediately_forAlisveris() {
        Classified classified = withCategory(ClassifiedCategory.ALISVERIS);

        classified.prePersist();

        assertThat(classified.getStatus()).isEqualTo(ClassifiedStatus.AKTIF);
    }

    @Test
    void prePersist_shouldKeepExplicitStatus() {
        Classified classified = withCategory(ClassifiedCategory.EMLAK);
        classified.setStatus(ClassifiedStatus.DEAKTIF);

        classified.prePersist();

        assertThat(classified.getStatus()).isEqualTo(ClassifiedStatus.DEAKTIF);
    }

    @ParameterizedTest
    @EnumSource(ClassifiedCategory.class)
    void prePersist_shouldDeriveEndDateFromCategoryDuration(ClassifiedCategory category) {
        Classified classified = withCategory(category);
        LocalDateTime before = LocalDateTime.now().plusWeeks(category.getDurationInWeeks());

        classified.prePersist();

        LocalDateTime after = LocalDateTime.now().plusWeeks(category.getDurationInWeeks());
        assertThat(classified.getEndDate()).isBetween(before, after);
    }

    @Test
    void prePersist_shouldKeepExplicitEndDate() {
        Classified classified = withCategory(ClassifiedCategory.EMLAK);
        LocalDateTime fixed = LocalDateTime.of(2030, 1, 1, 12, 0);
        classified.setEndDate(fixed);

        classified.prePersist();

        assertThat(classified.getEndDate()).isEqualTo(fixed);
    }
}

package com.marketplace.classifieds.domain.enums;

import lombok.Getter;

@Getter
public enum ClassifiedCategory {
    EMLAK("Emlak", 4),
    VASITA("Vasıta", 3),
    ALISVERIS("Alışveriş", 8),
    DIGER("Diğer", 8);

    private final String displayName;
    private final int durationInWeeks;

    ClassifiedCategory(String displayName, int durationInWeeks) {
        this.displayName = displayName;
        this.durationInWeeks = durationInWeeks;
    }

    public boolean requiresApproval() {
        return this != ALISVERIS;
    }
}
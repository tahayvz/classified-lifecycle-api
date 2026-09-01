package com.marketplace.classifieds.domain.enums;

import java.util.EnumSet;
import java.util.Set;

public enum ClassifiedStatus {

    ONAY_BEKLIYOR,
    AKTIF,
    DEAKTIF;

    private Set<ClassifiedStatus> nextStates;

    static {
        ONAY_BEKLIYOR.nextStates = EnumSet.of(AKTIF, DEAKTIF);
        AKTIF.nextStates = EnumSet.of(DEAKTIF);
        DEAKTIF.nextStates = EnumSet.noneOf(ClassifiedStatus.class);
    }

    public boolean canTransitionTo(ClassifiedStatus next) {
        return nextStates.contains(next);
    }
}

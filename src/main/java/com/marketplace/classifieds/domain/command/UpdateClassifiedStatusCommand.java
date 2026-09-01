package com.marketplace.classifieds.domain.command;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;

public record UpdateClassifiedStatusCommand(
        ClassifiedStatus status,
        String reason,
        String changedBy
) {
}

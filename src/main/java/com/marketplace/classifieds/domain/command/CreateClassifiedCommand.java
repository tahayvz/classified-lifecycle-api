package com.marketplace.classifieds.domain.command;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;

public record CreateClassifiedCommand(
        String title,
        String description,
        ClassifiedCategory category,
        String createdBy
) {
}

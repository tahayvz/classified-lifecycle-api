package com.marketplace.classifieds.domain.model;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public record ClassifiedStatistics(Map<ClassifiedStatus, Long> countByStatus, long total) {

    public ClassifiedStatistics {
        countByStatus = (countByStatus == null || countByStatus.isEmpty())
                ? Map.of()
                : Collections.unmodifiableMap(new EnumMap<>(countByStatus));
    }

    public static ClassifiedStatistics of(Map<ClassifiedStatus, Long> countByStatus) {
        long total = countByStatus == null
                ? 0L
                : countByStatus.values().stream()
                        .mapToLong(count -> count == null ? 0L : count)
                        .sum();

        return new ClassifiedStatistics(countByStatus, total);
    }
}

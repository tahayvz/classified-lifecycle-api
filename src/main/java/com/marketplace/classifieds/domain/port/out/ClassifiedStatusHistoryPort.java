package com.marketplace.classifieds.domain.port.out;

import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;

import java.util.List;

public interface ClassifiedStatusHistoryPort {

    ClassifiedStatusHistory save(ClassifiedStatusHistory history);

    List<ClassifiedStatusHistory> findByClassifiedIdOrderByChangedAtDesc(Long classifiedId);
}

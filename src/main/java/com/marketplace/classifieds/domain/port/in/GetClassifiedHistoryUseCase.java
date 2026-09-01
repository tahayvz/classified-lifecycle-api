package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;

import java.util.List;

public interface GetClassifiedHistoryUseCase {

    List<ClassifiedStatusHistory> getStatusHistory(Long id);
}

package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.domain.model.ClassifiedStatistics;

public interface GetStatisticsUseCase {

    ClassifiedStatistics getStatistics();
}

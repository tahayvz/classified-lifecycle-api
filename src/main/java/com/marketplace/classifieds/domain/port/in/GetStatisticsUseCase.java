package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedStatisticsResponse;

public interface GetStatisticsUseCase {

    ClassifiedStatisticsResponse getStatistics();
}

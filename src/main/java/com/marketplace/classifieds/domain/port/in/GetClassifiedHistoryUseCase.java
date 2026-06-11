package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.adapter.in.web.dto.response.StatusHistoryResponse;

import java.util.List;

public interface GetClassifiedHistoryUseCase {

    List<StatusHistoryResponse> getStatusHistory(Long id);
}

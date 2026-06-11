package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.adapter.in.web.dto.request.UpdateClassifiedStatusRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;

public interface UpdateClassifiedStatusUseCase {

    ClassifiedResponse updateStatus(Long id, UpdateClassifiedStatusRequest request, String changedBy);
}

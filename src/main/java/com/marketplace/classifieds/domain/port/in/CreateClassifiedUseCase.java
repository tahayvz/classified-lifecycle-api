package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.adapter.in.web.dto.request.CreateClassifiedRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;

public interface CreateClassifiedUseCase {

    ClassifiedResponse create(CreateClassifiedRequest request);
}

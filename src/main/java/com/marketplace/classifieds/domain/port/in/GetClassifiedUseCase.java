package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;

public interface GetClassifiedUseCase {

    ClassifiedResponse get(Long id);
}

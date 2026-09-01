package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.domain.model.Classified;

public interface GetClassifiedUseCase {

    Classified get(Long id);
}

package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.command.CreateClassifiedCommand;

public interface CreateClassifiedUseCase {

    Classified create(CreateClassifiedCommand command);
}

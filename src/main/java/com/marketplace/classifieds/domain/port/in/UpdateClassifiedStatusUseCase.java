package com.marketplace.classifieds.domain.port.in;

import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.command.UpdateClassifiedStatusCommand;

public interface UpdateClassifiedStatusUseCase {

    Classified updateStatus(Long id, UpdateClassifiedStatusCommand command);
}

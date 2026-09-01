package com.marketplace.classifieds.domain.port.out;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.model.Classified;

import java.util.Map;
import java.util.Optional;

public interface ClassifiedPort {

    Classified save(Classified classified);

    Optional<Classified> findById(Long id);

    boolean existsByTitleAndDescription(String title, String description, ClassifiedCategory category);

    Map<ClassifiedStatus, Long> countByStatus();
}

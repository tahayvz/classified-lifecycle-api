package com.marketplace.classifieds.adapter.out.persistence;

import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedJpaRepository;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClassifiedPersistenceAdapter implements ClassifiedPort {

    private final ClassifiedJpaRepository repository;

    @Override
    public Classified save(Classified classified) {
        return repository.save(classified);
    }

    @Override
    public Optional<Classified> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsByTitleAndDescription(String title, String description, ClassifiedCategory category) {
        return repository.existsByTitleAndDescriptionAndCategory(title, description, category);
    }

    @Override
    public List<Object[]> countByStatusGrouped() {
        return repository.countByStatusGrouped();
    }
}

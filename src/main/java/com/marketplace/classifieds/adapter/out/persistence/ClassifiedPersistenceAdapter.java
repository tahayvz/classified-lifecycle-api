package com.marketplace.classifieds.adapter.out.persistence;

import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedJpaRepository;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
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
    public Map<ClassifiedStatus, Long> countByStatus() {
        Map<ClassifiedStatus, Long> counts = new EnumMap<>(ClassifiedStatus.class);

        for (Object[] row : repository.countByStatusGrouped()) {
            if (row[0] instanceof ClassifiedStatus status) {
                counts.put(status, row[1] == null ? 0L : ((Number) row[1]).longValue());
            }
        }

        return counts;
    }
}

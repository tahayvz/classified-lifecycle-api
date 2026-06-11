package com.marketplace.classifieds.adapter.out.persistence;

import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedStatusHistoryJpaRepository;
import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClassifiedStatusHistoryPersistenceAdapter implements ClassifiedStatusHistoryPort {

    private final ClassifiedStatusHistoryJpaRepository repository;

    @Override
    public ClassifiedStatusHistory save(ClassifiedStatusHistory history) {
        return repository.save(history);
    }

    @Override
    public List<ClassifiedStatusHistory> findByClassifiedIdOrderByChangedAtDesc(Long classifiedId) {
        return repository.findByClassifiedIdOrderByChangedAtDesc(classifiedId);
    }
}

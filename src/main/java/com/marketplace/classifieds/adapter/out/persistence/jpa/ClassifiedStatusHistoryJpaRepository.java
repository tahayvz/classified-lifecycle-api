package com.marketplace.classifieds.adapter.out.persistence.jpa;

import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassifiedStatusHistoryJpaRepository extends JpaRepository<ClassifiedStatusHistory, Long> {

    List<ClassifiedStatusHistory> findByClassifiedIdOrderByChangedAtDesc(Long classifiedId);
}
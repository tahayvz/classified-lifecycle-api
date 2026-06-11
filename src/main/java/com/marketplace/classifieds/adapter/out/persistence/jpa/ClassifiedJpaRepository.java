package com.marketplace.classifieds.adapter.out.persistence.jpa;

import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassifiedJpaRepository extends JpaRepository<Classified, Long> {

    boolean existsByTitleAndDescriptionAndCategory(
            String title,
            String description,
            ClassifiedCategory category
    );

    long countByStatus(ClassifiedStatus status);

    @Query("SELECT c.status, COUNT(c) FROM Classified c GROUP BY c.status")
    List<Object[]> countByStatusGrouped();

    List<Classified> findByStatus(ClassifiedStatus status);
}
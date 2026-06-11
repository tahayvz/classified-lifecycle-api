package com.marketplace.classifieds.domain.model;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "classifieds", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_title_description", columnList = "title, description")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classified {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassifiedCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassifiedStatus status;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = category.requiresApproval()
                    ? ClassifiedStatus.ONAY_BEKLIYOR
                    : ClassifiedStatus.AKTIF;
        }

        if (this.endDate == null) {
            this.endDate = LocalDateTime.now()
                    .plusWeeks(category.getDurationInWeeks());
        }
    }
}
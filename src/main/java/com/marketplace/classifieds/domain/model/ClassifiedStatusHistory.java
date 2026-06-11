package com.marketplace.classifieds.domain.model;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "classified_status_history", indexes = {
        @Index(name = "idx_classified_id", columnList = "classified_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassifiedStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classified_id", nullable = false)
    private Classified classified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassifiedStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassifiedStatus newStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(nullable = false)
    private String changedBy;

    private String reason;
}
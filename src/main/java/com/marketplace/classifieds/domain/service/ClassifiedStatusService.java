package com.marketplace.classifieds.domain.service;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.exception.ImmutableClassifiedException;
import com.marketplace.classifieds.domain.exception.InvalidStatusTransitionException;
import com.marketplace.classifieds.domain.exception.SameStatusException;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;

public class ClassifiedStatusService {

    private final ClassifiedStatusHistoryPort historyPort;

    public ClassifiedStatusService(ClassifiedStatusHistoryPort historyPort) {
        this.historyPort = historyPort;
    }

    public Classified changeStatus(Classified classified, ClassifiedStatus newStatus,
                                   String changedBy, String reason) {

        ClassifiedStatus previous = classified.getStatus();

        validateTransition(previous, newStatus, classified);

        classified.setStatus(newStatus);
        saveHistory(classified, previous, newStatus, changedBy, reason);

        return classified;
    }

    private void validateTransition(ClassifiedStatus previous, ClassifiedStatus next, Classified classified) {

        if (previous == ClassifiedStatus.MUKERRER) {
            throw new ImmutableClassifiedException("Mükerrer ilanların durumu güncellenemez. İlan id: " + classified.getId());
        }

        if (previous == next) {
            throw new SameStatusException("İlan zaten bu durumda: " + previous + ". İlan id: " + classified.getId());
        }

        if (!previous.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException("Geçersiz durum geçişi: " + previous + " → " + next
                    + " (İlan id: " + classified.getId() + ")");
        }
    }

    private void saveHistory(Classified classified, ClassifiedStatus previous, ClassifiedStatus next,
                             String changedBy, String reason) {

        historyPort.save(
                ClassifiedStatusHistory.builder()
                        .classified(classified)
                        .previousStatus(previous)
                        .newStatus(next)
                        .changedBy(changedBy)
                        .reason(reason)
                        .build()
        );
    }
}

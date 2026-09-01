package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.domain.exception.ClassifiedNotFoundException;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.model.ClassifiedStatistics;
import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import com.marketplace.classifieds.domain.port.in.CreateClassifiedUseCase;
import com.marketplace.classifieds.domain.port.in.GetClassifiedHistoryUseCase;
import com.marketplace.classifieds.domain.port.in.GetClassifiedUseCase;
import com.marketplace.classifieds.domain.port.in.GetStatisticsUseCase;
import com.marketplace.classifieds.domain.port.in.UpdateClassifiedStatusUseCase;
import com.marketplace.classifieds.domain.command.CreateClassifiedCommand;
import com.marketplace.classifieds.domain.command.UpdateClassifiedStatusCommand;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassifiedService implements
        CreateClassifiedUseCase,
        GetClassifiedUseCase,
        UpdateClassifiedStatusUseCase,
        GetClassifiedHistoryUseCase,
        GetStatisticsUseCase {

    private final ClassifiedPort classifiedPort;
    private final ClassifiedStatusHistoryPort historyPort;

    private final ClassifiedValidationService validationService;
    private final ClassifiedStatusService statusService;

    @Override
    public Classified create(CreateClassifiedCommand command) {

        validationService.validateBadWords(command.title(), command.description());
        validationService.validateDuplicate(command.title(), command.description(), command.category());

        return classifiedPort.save(
                Classified.builder()
                        .title(command.title())
                        .description(command.description())
                        .category(command.category())
                        .createdBy(command.createdBy())
                        .build()
        );
    }

    @Override
    public Classified get(Long id) {
        return find(id);
    }

    @Override
    public Classified updateStatus(Long id, UpdateClassifiedStatusCommand command) {
        Classified classified = find(id);

        statusService.changeStatus(classified, command.status(), command.changedBy(), command.reason());

        return classifiedPort.save(classified);
    }

    @Override
    public List<ClassifiedStatusHistory> getStatusHistory(Long id) {
        find(id);

        return historyPort.findByClassifiedIdOrderByChangedAtDesc(id);
    }

    @Override
    public ClassifiedStatistics getStatistics() {
        return ClassifiedStatistics.of(classifiedPort.countByStatus());
    }

    private Classified find(Long id) {
        return classifiedPort.findById(id)
                .orElseThrow(() -> new ClassifiedNotFoundException("İlan bulunamadı: " + id));
    }
}

package com.marketplace.classifieds.application.service;

import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.exception.ClassifiedNotFoundException;

import com.marketplace.classifieds.domain.port.in.CreateClassifiedUseCase;
import com.marketplace.classifieds.domain.port.in.GetClassifiedUseCase;
import com.marketplace.classifieds.domain.port.in.UpdateClassifiedStatusUseCase;
import com.marketplace.classifieds.domain.port.in.GetClassifiedHistoryUseCase;
import com.marketplace.classifieds.domain.port.in.GetStatisticsUseCase;

import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;

import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;
import com.marketplace.classifieds.adapter.in.web.dto.request.CreateClassifiedRequest;
import com.marketplace.classifieds.adapter.in.web.dto.request.UpdateClassifiedStatusRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedStatisticsResponse;
import com.marketplace.classifieds.adapter.in.web.dto.response.StatusHistoryResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ClassifiedResponse create(CreateClassifiedRequest request) {

        validationService.validateBadWords(request.getTitle(), request.getDescription());
        validationService.validateDuplicate(request.getTitle(), request.getDescription(), request.getCategory());

        Classified saved = classifiedPort.save(
                Classified.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .category(request.getCategory())
                        .createdBy("system")
                        .build()
        );

        return toResponse(saved);
    }

    @Override
    public ClassifiedResponse get(Long id) {
        return toResponse(find(id));
    }

    @Override
    public ClassifiedResponse updateStatus(Long id, UpdateClassifiedStatusRequest request, String changedBy) {
        Classified c = find(id);

        statusService.changeStatus(c, request.getStatus(), changedBy, request.getReason());

        Classified updated = classifiedPort.save(c);

        return toResponse(updated);
    }

    @Override
    public List<StatusHistoryResponse> getStatusHistory(Long id) {
        find(id);

        return historyPort
                .findByClassifiedIdOrderByChangedAtDesc(id)
                .stream()
                .map(h -> StatusHistoryResponse.builder()
                        .id(h.getId())
                        .previousStatus(h.getPreviousStatus())
                        .newStatus(h.getNewStatus())
                        .changedAt(h.getChangedAt())
                        .changedBy(h.getChangedBy())
                        .reason(h.getReason())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ClassifiedStatisticsResponse getStatistics() {
        var grouped = classifiedPort.countByStatusGrouped();

        Map<String, Long> stats = grouped.stream()
                .collect(Collectors.toMap(
                        row -> row[0] == null ? "UNKNOWN" : row[0].toString(),
                        row -> row[1] == null ? 0L : (Long) row[1]
                ));

        long total = stats.values().stream().mapToLong(Long::longValue).sum();

        return ClassifiedStatisticsResponse.builder()
                .statistics(stats)
                .totalClassifieds(total)
                .build();
    }

    private Classified find(Long id) {
        return classifiedPort.findById(id)
                .orElseThrow(() -> new ClassifiedNotFoundException("İlan bulunamadı: " + id));
    }

    private ClassifiedResponse toResponse(Classified c) {
        return ClassifiedResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .status(c.getStatus())
                .endDate(c.getEndDate())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .build();
    }
}

package com.marketplace.classifieds.adapter.in.web.mapper;

import com.marketplace.classifieds.adapter.in.web.dto.request.CreateClassifiedRequest;
import com.marketplace.classifieds.adapter.in.web.dto.request.UpdateClassifiedStatusRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedStatisticsResponse;
import com.marketplace.classifieds.adapter.in.web.dto.response.StatusHistoryResponse;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.model.ClassifiedStatistics;
import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import com.marketplace.classifieds.domain.command.CreateClassifiedCommand;
import com.marketplace.classifieds.domain.command.UpdateClassifiedStatusCommand;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClassifiedWebMapper {

    private ClassifiedWebMapper() {
    }

    public static CreateClassifiedCommand toCommand(CreateClassifiedRequest request, String createdBy) {
        return new CreateClassifiedCommand(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                createdBy
        );
    }

    public static UpdateClassifiedStatusCommand toCommand(UpdateClassifiedStatusRequest request, String changedBy) {
        return new UpdateClassifiedStatusCommand(
                request.getStatus(),
                request.getReason(),
                changedBy
        );
    }

    public static ClassifiedResponse toResponse(Classified classified) {
        return ClassifiedResponse.builder()
                .id(classified.getId())
                .title(classified.getTitle())
                .description(classified.getDescription())
                .category(classified.getCategory())
                .status(classified.getStatus())
                .endDate(classified.getEndDate())
                .createdAt(classified.getCreatedAt())
                .updatedAt(classified.getUpdatedAt())
                .createdBy(classified.getCreatedBy())
                .build();
    }

    public static List<StatusHistoryResponse> toHistoryResponses(List<ClassifiedStatusHistory> history) {
        return history.stream().map(ClassifiedWebMapper::toResponse).toList();
    }

    public static StatusHistoryResponse toResponse(ClassifiedStatusHistory history) {
        return StatusHistoryResponse.builder()
                .id(history.getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .changedAt(history.getChangedAt())
                .changedBy(history.getChangedBy())
                .reason(history.getReason())
                .build();
    }

    public static ClassifiedStatisticsResponse toResponse(ClassifiedStatistics statistics) {
        Map<String, Long> byName = new LinkedHashMap<>();

        for (Map.Entry<ClassifiedStatus, Long> entry : statistics.countByStatus().entrySet()) {
            byName.put(entry.getKey().name(), entry.getValue());
        }

        return ClassifiedStatisticsResponse.builder()
                .statistics(byName)
                .totalClassifieds(statistics.total())
                .build();
    }
}

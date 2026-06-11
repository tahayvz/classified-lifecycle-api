package com.marketplace.classifieds.adapter.in.web.dto.request;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateClassifiedStatusRequest {

    @NotNull(message = "Durum bilgisi gereklidir")
    private ClassifiedStatus status;

    private String reason;
}
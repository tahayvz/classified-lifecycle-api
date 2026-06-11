package com.marketplace.classifieds.adapter.in.web.dto.response;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Bir ilana ait durum değişikliği kaydını temsil eden model")
public class StatusHistoryResponse {

    @Schema(description = "Durum değişikliği kaydının benzersiz kimliği", example = "501")
    private Long id;

    @Schema(
            description = "Önceki ilan durumu (ONAY_BEKLIYOR, ACTIVE, DEACTIVE, MUKERRER)",
            example = "ONAY_BEKLIYOR"
    )
    private ClassifiedStatus previousStatus;

    @Schema(
            description = "Yeni ilan durumu (ONAY_BEKLIYOR, ACTIVE, DEACTIVE, MUKERRER)",
            example = "ACTIVE"
    )
    private ClassifiedStatus newStatus;

    @Schema(
            description = "Durum değişikliğinin yapıldığı tarih ve saat (ISO 8601 formatı)",
            example = "2025-11-14T11:12:45.999"
    )
    private LocalDateTime changedAt;

    @Schema(
            description = "Durumu değiştiren kullanıcı",
            example = "system"
    )
    private String changedBy;

    @Schema(
            description = "Durum değişikliğinin nedeni. Örneğin: 'Kullanıcı tarafından deaktif edildi'.",
            example = "Kullanıcı tarafından deaktif edildi"
    )
    private String reason;
}

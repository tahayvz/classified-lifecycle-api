package com.marketplace.classifieds.adapter.in.web.dto.response;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
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
@Schema(description = "İlan detaylarını temsil eden response modeli")
public class ClassifiedResponse {

    @Schema(description = "İlanın benzersiz kimliği", example = "12345")
    private Long id;

    @Schema(description = "İlan başlığı", example = "Sıfır ayarında iPhone 16 Pro Max")
    private String title;

    @Schema(description = "İlan açıklaması", example = "Kutulu, faturalı, temiz kullanıldı.")
    private String description;

    @Schema(
            description = "İlan kategorisi (Emlak, Vasıta, Alışveriş, Diğer)",
            example = "ALISVERIS"
    )
    private ClassifiedCategory category;

    @Schema(
            description = "İlanın mevcut durumu (ONAY_BEKLIYOR, AKTIF, DEAKTIF)",
            example = "AKTIF"
    )
    private ClassifiedStatus status;

    @Schema(
            description = "İlanın bitiş tarihi. Kategoriye göre otomatik belirlenir. "
                    + "Emlak: 4 hafta, Vasıta: 3 hafta, Alışveriş/Diğer: 8 hafta.",
            example = "2025-12-20T23:59:59.999"
    )
    private LocalDateTime endDate;

    @Schema(
            description = "İlanın oluşturulma tarihi. ISO 8601 formatı.",
            example = "2025-11-14T18:28:08.685"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "İlanın son güncellenme tarihi. ISO 8601 formatı.",
            example = "2025-11-14T11:02:10.999"
    )
    private LocalDateTime updatedAt;

    @Schema(
            description = "İlanı oluşturan kullanıcı",
            example = "system"
    )
    private String createdBy;
}

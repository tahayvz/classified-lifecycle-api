package com.marketplace.classifieds.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dashboard üzerinde gösterilen ilan istatistiklerini temsil eden model")
public class ClassifiedStatisticsResponse {

    @Schema(
            description = "Kategori veya durum bazlı istatistikler. Key: kategori/durum adı, Value: sayı.",
            example = "{\"AKTIF\": 1200, \"ONAY_BEKLIYOR\": 340, \"DEAKTIF\": 50}"
    )
    private Map<String, Long> statistics;

    @Schema(
            description = "Sistemdeki toplam ilan sayısı",
            example = "1590"
    )
    private Long totalClassifieds;
}

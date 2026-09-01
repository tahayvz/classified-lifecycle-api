package com.marketplace.classifieds.adapter.in.web.controller;

import com.marketplace.classifieds.domain.port.in.GetStatisticsUseCase;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedStatisticsResponse;
import com.marketplace.classifieds.adapter.in.web.mapper.ClassifiedWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "İstatistik API'ları")
public class DashboardController {

    private final GetStatisticsUseCase getStatisticsUseCase;

    @Operation(
            summary = "İlan istatistiklerini getir",
            description = "Toplam ilan sayısı, aktif ilan sayısı, pasif ilan sayısı gibi yüksek seviye istatistikleri döner."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "İstatistikler başarıyla getirildi",
                    content = @Content(schema = @Schema(implementation = ClassifiedStatisticsResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Beklenmeyen bir hata oluştu")
    })
    @GetMapping("/statistics")
    public ClassifiedStatisticsResponse getStatistics() {
        return ClassifiedWebMapper.toResponse(getStatisticsUseCase.getStatistics());
    }
}

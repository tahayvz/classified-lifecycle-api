package com.marketplace.classifieds.adapter.in.web.controller;

import com.marketplace.classifieds.domain.port.in.*;
import com.marketplace.classifieds.adapter.in.web.dto.request.CreateClassifiedRequest;
import com.marketplace.classifieds.adapter.in.web.dto.request.UpdateClassifiedStatusRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;
import com.marketplace.classifieds.adapter.in.web.dto.response.StatusHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classifieds")
@Tag(name = "Classifieds", description = "İlan CRUD & Durum Yönetimi")
public class ClassifiedController {

    private final CreateClassifiedUseCase createClassifiedUseCase;
    private final GetClassifiedUseCase getClassifiedUseCase;
    private final UpdateClassifiedStatusUseCase updateClassifiedStatusUseCase;
    private final GetClassifiedHistoryUseCase getClassifiedHistoryUseCase;

    @Operation(
            summary = "İlan oluştur",
            description = "Yeni bir ilan oluşturur. Validation, duplicate kontrolü ve kötü kelime filtrelemesi uygulanır."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "İlan başarıyla oluşturuldu",
                    content = @Content(schema = @Schema(implementation = ClassifiedResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validasyon hatası"),
            @ApiResponse(responseCode = "409", description = "Mükerrer ilan hatası")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ClassifiedResponse create(@Valid @RequestBody CreateClassifiedRequest request) {
        return createClassifiedUseCase.create(request);
    }

    @Operation(
            summary = "İlan detay getir",
            description = "Belirtilen ID'ye göre ilan detaylarını döner."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "İlan bulundu",
                    content = @Content(schema = @Schema(implementation = ClassifiedResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "İlan bulunamadı")
    })
    @GetMapping("/{id}")
    public ClassifiedResponse get(@PathVariable Long id) {
        return getClassifiedUseCase.get(id);
    }

    @Operation(
            summary = "İlan durumunu güncelle",
            description = "İlan durumunu (ONAY_BEKLIYOR, AKTIF, DEAKTIF, MUKERRER) günceller ve geçmişe kayıt ekler."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "İlan durumu güncellendi",
                    content = @Content(schema = @Schema(implementation = ClassifiedResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Geçersiz durum bilgisi"),
            @ApiResponse(responseCode = "404", description = "İlan bulunamadı")
    })
    @PutMapping("/{id}/status")
    public ClassifiedResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClassifiedStatusRequest request
    ) {
        return updateClassifiedStatusUseCase.updateStatus(id, request, "system");
    }

    @Operation(
            summary = "İlan durum geçmişi",
            description = "İlan için bugüne kadar yapılan tüm durum değişikliklerini listeler."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Durum geçmişi listelendi",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = StatusHistoryResponse.class)
                    ))
            ),
            @ApiResponse(responseCode = "404", description = "İlan bulunamadı")
    })
    @GetMapping("/{id}/history")
    public List<StatusHistoryResponse> getHistory(@PathVariable Long id) {
        return getClassifiedHistoryUseCase.getStatusHistory(id);
    }
}

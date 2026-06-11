package com.marketplace.classifieds.adapter.in.web.dto.request;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClassifiedRequest {

    @NotBlank(message = "İlan başlığı boş olamaz")
    @Size(min = 10, max = 50, message = "İlan başlığı 10-50 karakter arasında olmalıdır")
    @Pattern(regexp = "^[a-zA-Z0-9çÇğĞıİöÖşŞüÜ].*",
            message = "İlan başlığı harf veya rakam ile başlamalıdır")
    private String title;

    @NotBlank(message = "İlan açıklaması boş olamaz")
    @Size(min = 20, max = 200, message = "İlan açıklaması 20-200 karakter arasında olmalıdır")
    private String description;

    @NotNull(message = "Kategori seçilmelidir")
    private ClassifiedCategory category;
}
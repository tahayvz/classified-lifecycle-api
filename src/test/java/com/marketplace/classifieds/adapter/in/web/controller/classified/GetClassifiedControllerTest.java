package com.marketplace.classifieds.adapter.in.web.controller.classified;

import com.marketplace.classifieds.adapter.in.web.controller.ClassifiedController;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;
import com.marketplace.classifieds.adapter.in.web.exception.GlobalExceptionHandler;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.exception.ClassifiedNotFoundException;
import com.marketplace.classifieds.domain.port.in.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassifiedController.class)
@Import({GetClassifiedControllerTest.MockConfig.class, GlobalExceptionHandler.class})
class GetClassifiedControllerTest {

    static GetClassifiedUseCase getClassifiedUseCase = Mockito.mock(GetClassifiedUseCase.class);

    static class MockConfig {

        @Bean
        CreateClassifiedUseCase createClassifiedUseCase() {
            return Mockito.mock(CreateClassifiedUseCase.class);
        }

        @Bean
        GetClassifiedUseCase getClassifiedUseCase() {
            return GetClassifiedControllerTest.getClassifiedUseCase;
        }

        @Bean
        UpdateClassifiedStatusUseCase updateClassifiedStatusUseCase() {
            return Mockito.mock(UpdateClassifiedStatusUseCase.class);
        }

        @Bean
        GetClassifiedHistoryUseCase getClassifiedHistoryUseCase() {
            return Mockito.mock(GetClassifiedHistoryUseCase.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(getClassifiedUseCase);
    }

    @Test
    void get_shouldReturn200_whenFound() throws Exception {
        ClassifiedResponse response = ClassifiedResponse.builder()
                .id(50L)
                .title("Satılık Araba")
                .description("Temiz araç")
                .category(ClassifiedCategory.VASITA)
                .status(ClassifiedStatus.AKTIF)
                .createdAt(LocalDateTime.now())
                .build();

        when(getClassifiedUseCase.get(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/v1/classifieds/50")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50L))
                .andExpect(jsonPath("$.title").value("Satılık Araba"))
                .andExpect(jsonPath("$.status").value("AKTIF"));
    }

    @Test
    void get_shouldReturn404_whenNotFound() throws Exception {

        when(getClassifiedUseCase.get(anyLong()))
                .thenThrow(new ClassifiedNotFoundException("İlan bulunamadı"));

        mockMvc.perform(get("/api/v1/classifieds/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("İlan bulunamadı"));
    }

    @Test
    void get_shouldReturn500_whenUnexpectedError() throws Exception {

        when(getClassifiedUseCase.get(anyLong()))
                .thenThrow(new RuntimeException("Patladı"));

        mockMvc.perform(get("/api/v1/classifieds/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Beklenmeyen bir hata oluştu: Patladı"));
    }

}

package com.marketplace.classifieds.adapter.in.web.controller.classified;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.classifieds.adapter.in.web.controller.ClassifiedController;
import com.marketplace.classifieds.adapter.in.web.dto.request.UpdateClassifiedStatusRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;
import com.marketplace.classifieds.adapter.in.web.exception.GlobalExceptionHandler;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.exception.ClassifiedNotFoundException;
import com.marketplace.classifieds.domain.exception.InvalidStatusTransitionException;
import com.marketplace.classifieds.domain.exception.SameStatusException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassifiedController.class)
@Import({UpdateClassifiedStatusControllerTest.MockConfig.class, GlobalExceptionHandler.class})
class UpdateClassifiedStatusControllerTest {

    static UpdateClassifiedStatusUseCase updateStatusUseCase = Mockito.mock(UpdateClassifiedStatusUseCase.class);

    static class MockConfig {

        @Bean
        CreateClassifiedUseCase createClassifiedUseCase() {
            return Mockito.mock(CreateClassifiedUseCase.class);
        }

        @Bean
        GetClassifiedUseCase getClassifiedUseCase() {
            return Mockito.mock(GetClassifiedUseCase.class);
        }

        @Bean
        UpdateClassifiedStatusUseCase updateClassifiedStatusUseCase() {
            return UpdateClassifiedStatusControllerTest.updateStatusUseCase;
        }

        @Bean
        GetClassifiedHistoryUseCase getClassifiedHistoryUseCase() {
            return Mockito.mock(GetClassifiedHistoryUseCase.class);
        }

    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(updateStatusUseCase);
    }

    @Test
    void updateStatus_shouldReturn200_whenSuccessful() throws Exception {
        UpdateClassifiedStatusRequest request =
                new UpdateClassifiedStatusRequest(ClassifiedStatus.DEAKTIF, "Sahibi değişti");

        ClassifiedResponse fakeResponse = ClassifiedResponse.builder()
                .id(10L)
                .title("Test")
                .description("desc")
                .category(ClassifiedCategory.VASITA)
                .status(ClassifiedStatus.DEAKTIF)
                .createdAt(LocalDateTime.now())
                .build();

        when(updateStatusUseCase.updateStatus(anyLong(), any(), any()))
                .thenReturn(fakeResponse);

        mockMvc.perform(put("/api/v1/classifieds/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEAKTIF"));
    }

    @Test
    void updateStatus_shouldReturn404_whenNotFound() throws Exception {
        UpdateClassifiedStatusRequest request =
                new UpdateClassifiedStatusRequest(ClassifiedStatus.AKTIF, "neden");

        when(updateStatusUseCase.updateStatus(anyLong(), any(), any()))
                .thenThrow(new ClassifiedNotFoundException("İlan bulunamadı"));

        mockMvc.perform(put("/api/v1/classifieds/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("İlan bulunamadı"));
    }

    @Test
    void updateStatus_shouldReturn409_whenInvalidTransition() throws Exception {
        UpdateClassifiedStatusRequest request =
                new UpdateClassifiedStatusRequest(ClassifiedStatus.AKTIF, "neden");

        when(updateStatusUseCase.updateStatus(anyLong(), any(), any()))
                .thenThrow(new InvalidStatusTransitionException("Geçersiz geçiş"));

        mockMvc.perform(put("/api/v1/classifieds/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Geçersiz geçiş"));
    }

    @Test
    void updateStatus_shouldReturn409_whenSameStatus() throws Exception {
        UpdateClassifiedStatusRequest request =
                new UpdateClassifiedStatusRequest(ClassifiedStatus.AKTIF, "neden");

        when(updateStatusUseCase.updateStatus(anyLong(), any(), any()))
                .thenThrow(new SameStatusException("İlan zaten bu durumda"));

        mockMvc.perform(put("/api/v1/classifieds/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("İlan zaten bu durumda"));
    }

    @Test
    void updateStatus_shouldReturn500_whenUnexpected() throws Exception {
        UpdateClassifiedStatusRequest request =
                new UpdateClassifiedStatusRequest(ClassifiedStatus.DEAKTIF, "neden");

        when(updateStatusUseCase.updateStatus(anyLong(), any(), any()))
                .thenThrow(new RuntimeException("Patladı"));

        mockMvc.perform(put("/api/v1/classifieds/77/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Beklenmeyen bir hata oluştu: Patladı"));
    }

}

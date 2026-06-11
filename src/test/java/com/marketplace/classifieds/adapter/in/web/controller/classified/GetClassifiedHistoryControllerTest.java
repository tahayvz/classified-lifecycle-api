package com.marketplace.classifieds.adapter.in.web.controller.classified;

import com.marketplace.classifieds.adapter.in.web.controller.ClassifiedController;
import com.marketplace.classifieds.adapter.in.web.dto.response.StatusHistoryResponse;
import com.marketplace.classifieds.adapter.in.web.exception.GlobalExceptionHandler;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassifiedController.class)
@Import({GetClassifiedHistoryControllerTest.MockConfig.class, GlobalExceptionHandler.class})
class GetClassifiedHistoryControllerTest {

    static GetClassifiedHistoryUseCase historyUseCase = Mockito.mock(GetClassifiedHistoryUseCase.class);

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
            return Mockito.mock(UpdateClassifiedStatusUseCase.class);
        }

        @Bean
        GetClassifiedHistoryUseCase getClassifiedHistoryUseCase() {
            return GetClassifiedHistoryControllerTest.historyUseCase;
        }
    }

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(historyUseCase);
    }

    @Test
    void history_shouldReturn200_withList() throws Exception {

        List<StatusHistoryResponse> fakeHistory = List.of(
                StatusHistoryResponse.builder()
                        .id(1L)
                        .previousStatus(ClassifiedStatus.ONAY_BEKLIYOR)
                        .newStatus(ClassifiedStatus.AKTIF)
                        .changedAt(LocalDateTime.now())
                        .changedBy("system")
                        .reason("Aktif edildi")
                        .build(),
                StatusHistoryResponse.builder()
                        .id(2L)
                        .previousStatus(ClassifiedStatus.AKTIF)
                        .newStatus(ClassifiedStatus.DEAKTIF)
                        .changedAt(LocalDateTime.now())
                        .changedBy("admin")
                        .reason("Deaktif edildi")
                        .build()
        );

        when(historyUseCase.getStatusHistory(5L)).thenReturn(fakeHistory);

        mockMvc.perform(get("/api/v1/classifieds/5/history")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].previousStatus").value("ONAY_BEKLIYOR"))
                .andExpect(jsonPath("$[0].newStatus").value("AKTIF"))
                .andExpect(jsonPath("$[1].previousStatus").value("AKTIF"))
                .andExpect(jsonPath("$[1].newStatus").value("DEAKTIF"));
    }


    @Test
    void getHistory_shouldReturn200_whenFound() throws Exception {
        StatusHistoryResponse h1 = StatusHistoryResponse.builder()
                .id(1L)
                .previousStatus(ClassifiedStatus.ONAY_BEKLIYOR)
                .newStatus(ClassifiedStatus.AKTIF)
                .changedAt(LocalDateTime.now())
                .changedBy("system")
                .reason("Onaylandı")
                .build();

        when(historyUseCase.getStatusHistory(anyLong()))
                .thenReturn(List.of(h1));

        mockMvc.perform(get("/api/v1/classifieds/10/history")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void history_shouldReturn200_whenEmpty() throws Exception {
        when(historyUseCase.getStatusHistory(10L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/classifieds/10/history")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getHistory_shouldReturn404_whenNotFound() throws Exception {
        when(historyUseCase.getStatusHistory(anyLong()))
                .thenThrow(new ClassifiedNotFoundException("İlan bulunamadı"));

        mockMvc.perform(get("/api/v1/classifieds/999/history")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("İlan bulunamadı"));
    }

    @Test
    void history_shouldReturn500_whenUnexpected() throws Exception {
        when(historyUseCase.getStatusHistory(anyLong()))
                .thenThrow(new RuntimeException("Patladı"));

        mockMvc.perform(get("/api/v1/classifieds/33/history"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Beklenmeyen bir hata oluştu: Patladı"));
    }
}

package com.marketplace.classifieds.adapter.in.web.controller.dashboard;

import com.marketplace.classifieds.adapter.in.web.controller.DashboardController;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedStatisticsResponse;
import com.marketplace.classifieds.adapter.in.web.exception.GlobalExceptionHandler;
import com.marketplace.classifieds.domain.port.in.GetStatisticsUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import({DashboardControllerTest.MockConfig.class, GlobalExceptionHandler.class})
class DashboardControllerTest {

    static GetStatisticsUseCase statisticsUseCase = Mockito.mock(GetStatisticsUseCase.class);

    static class MockConfig {

        @Bean
        GetStatisticsUseCase getStatisticsUseCase() {
            return DashboardControllerTest.statisticsUseCase;
        }
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void getStatistics_shouldReturn200_whenSuccessful() throws Exception {

        ClassifiedStatisticsResponse fakeStats = new ClassifiedStatisticsResponse(
                Map.of("Aktif", 70L, "Deaktif", 30L), 100L);

        when(statisticsUseCase.getStatistics()).thenReturn(fakeStats);

        mockMvc.perform(get("/api/v1/dashboard/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClassifieds").value(100))
                .andExpect(jsonPath("$.statistics.Aktif").value(70))
                .andExpect(jsonPath("$.statistics.Deaktif").value(30));
    }

    @Test
    void getStatistics_shouldReturn500_whenUnexpectedError() throws Exception {
        when(statisticsUseCase.getStatistics())
                .thenThrow(new RuntimeException("Patladı"));

        mockMvc.perform(get("/api/v1/dashboard/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Beklenmeyen bir hata oluştu: Patladı"));
    }

}

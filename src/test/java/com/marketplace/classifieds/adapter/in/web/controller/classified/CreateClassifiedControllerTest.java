package com.marketplace.classifieds.adapter.in.web.controller.classified;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.classifieds.adapter.in.web.controller.ClassifiedController;
import com.marketplace.classifieds.adapter.in.web.dto.request.CreateClassifiedRequest;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.command.CreateClassifiedCommand;
import com.marketplace.classifieds.adapter.in.web.exception.GlobalExceptionHandler;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.exception.BadWordException;
import com.marketplace.classifieds.domain.exception.DuplicateClassifiedException;
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
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassifiedController.class)
@Import({CreateClassifiedControllerTest.MockConfig.class, GlobalExceptionHandler.class})
class CreateClassifiedControllerTest {

    static CreateClassifiedUseCase createClassifiedUseCase = Mockito.mock(CreateClassifiedUseCase.class);

    static class MockConfig {

        @Bean
        CreateClassifiedUseCase createClassifiedUseCase() {
            return CreateClassifiedControllerTest.createClassifiedUseCase;
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
            return Mockito.mock(GetClassifiedHistoryUseCase.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(createClassifiedUseCase);
    }

    @Test
    void create_shouldReturn201_whenSuccessful() throws Exception {
        CreateClassifiedRequest request = new CreateClassifiedRequest(
                "Oturuma uygun ev",
                "Sahile 5 dakika mesafede geniş ve ferah daire",
                ClassifiedCategory.EMLAK
        );

        Classified fakeResponse = Classified.builder()
                .id(10L)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .status(null)
                .build();

        when(createClassifiedUseCase.create(any())).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/classifieds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Oturuma uygun ev"));
    }

    @Test
    void create_shouldReturn400_whenTitleTooShort() throws Exception {

        CreateClassifiedRequest req =
                new CreateClassifiedRequest("Kısa", "Geçerli açıklama", ClassifiedCategory.EMLAK);

        mockMvc.perform(post("/api/v1/classifieds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenBadWordFound() throws Exception {

        CreateClassifiedRequest req =
                new CreateClassifiedRequest("Opsiyonlu Başlık", "Bu açıklama 20 karakterden uzun", ClassifiedCategory.DIGER);

        when(createClassifiedUseCase.create(any(CreateClassifiedCommand.class)))
                .thenThrow(new BadWordException("İçerikte yasaklı kelime bulundu"));

        mockMvc.perform(post("/api/v1/classifieds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    void create_shouldReturn409_whenDuplicate() throws Exception {

        CreateClassifiedRequest req =
                new CreateClassifiedRequest("Aynı Başlık", "Bu açıklama 20 karakterden uzun", ClassifiedCategory.EMLAK);

        when(createClassifiedUseCase.create(any(CreateClassifiedCommand.class)))
                .thenThrow(new DuplicateClassifiedException("Aynı ilan daha önce eklenmiş."));

        mockMvc.perform(post("/api/v1/classifieds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Aynı ilan daha önce eklenmiş."));
    }

    @Test
    void create_shouldReturn500_whenUnexpectedError() throws Exception {

        CreateClassifiedRequest req =
                new CreateClassifiedRequest("Yeni Başlık", "Bu açıklama 20 karakterden uzun", ClassifiedCategory.DIGER);

        when(createClassifiedUseCase.create(any(CreateClassifiedCommand.class)))
                .thenThrow(new RuntimeException("Patladı"));

        mockMvc.perform(post("/api/v1/classifieds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Beklenmeyen bir hata oluştu: Patladı"));
    }
}

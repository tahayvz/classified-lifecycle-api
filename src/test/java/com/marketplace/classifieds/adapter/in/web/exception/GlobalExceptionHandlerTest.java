package com.marketplace.classifieds.adapter.in.web.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DummyController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("İlan bulunamadı"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn409_whenDuplicate() throws Exception {
        mockMvc.perform(get("/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Mükerrer ilan"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn400_whenBadWord() throws Exception {
        mockMvc.perform(get("/badword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Yasaklı kelime bulundu"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn409_whenInvalidStatusTransition() throws Exception {
        mockMvc.perform(get("/invalid"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Geçersiz geçiş"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn400_whenJsonParseError() throws Exception {
        mockMvc.perform(get("/parse"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn500_whenGenericException() throws Exception {
        mockMvc.perform(get("/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Beklenmeyen bir hata oluştu: Patladı"));
    }
}

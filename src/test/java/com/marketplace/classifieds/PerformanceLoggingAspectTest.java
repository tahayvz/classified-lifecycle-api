package com.marketplace.classifieds;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PerformanceLoggingAspectTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldLogSlowRequest(CapturedOutput output) throws Exception {

        mockMvc.perform(get("/slow"))
                .andExpect(status().isOk());

        String logs = output.getOut();

        assertThat(logs)
                .contains("Slow API detected")
                .contains("SlowTestController")
                .contains("slow");
    }
}

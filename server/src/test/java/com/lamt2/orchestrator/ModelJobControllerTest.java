package com.lamt2.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.controller.ModelJobController;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ModelJobController.class)
@Import(ModelJobControllerTest.ModelJobControllerConfiguration.class)
@ActiveProfiles("test")
public class ModelJobControllerTest {
    @Autowired
    public MockMvc mockMvc;

    @TestConfiguration
    public static class ModelJobControllerConfiguration {
        @Bean
        public ModelTrainingService modelTrainingService() {
            return Mockito.mock(ModelTrainingService.class);
        }
    }

    @Test
    public void testApi() throws Exception {
        JobParameter jobParameter = new JobParameter(10, 64, 0.012f, 10);
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/model/new_job")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobParameter)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.params.message").value("Send create job!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.params.*", Matchers.hasSize(1)));
    }
}

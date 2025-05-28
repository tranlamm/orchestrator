package com.lamt2.orchestrator;

import com.lamt2.orchestrator.controller.ModelQueryController;
import com.lamt2.orchestrator.service.model_query.ModelQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(ModelQueryControllerTest.ModelQueryControllerConfiguration.class)
@WebMvcTest(ModelQueryController.class)
public class ModelQueryControllerTest {
    @Autowired
    public MockMvc mockMvc;

    @TestConfiguration
    public static class ModelQueryControllerConfiguration {
        @Bean
        public ModelQueryService modelQueryService() {
            return new ModelQueryService();
        }
    }

    @Test
    public void testModelQuery() {

    }
}

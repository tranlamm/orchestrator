package com.lamt2.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.service.kafka.KafkaService;
import com.lamt2.orchestrator.service.model_query.ModelQueryService;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import com.lamt2.orchestrator.service.websocket.WebSocketService;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class KafkaServiceTest {

    @TestConfiguration
    public static class KafkaServiceTestConfiguration {
        @Bean
        KafkaService kafkaService() {
            return new KafkaService();
        }
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    KafkaService kafkaService;

    @Mock
    private ModelTrainingService modelTrainingService;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private ModelQueryService modelQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testKafkaService() {

    }
}

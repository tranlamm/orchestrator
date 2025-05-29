package com.lamt2.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.configuration.kafka.KafkaConsumerConfig;
import com.lamt2.orchestrator.model.kafka.ModelInitData;
import com.lamt2.orchestrator.service.kafka.KafkaService;
import com.lamt2.orchestrator.service.model_query.ModelQueryService;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import com.lamt2.orchestrator.service.websocket.WebSocketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({KafkaServiceTest.KafkaServiceTestConfiguration.class, KafkaConsumerConfig.class, KafkaProducerConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
public class KafkaServiceTest {

    @TestConfiguration
    public static class KafkaServiceTestConfiguration {
        @Bean
        public KafkaService kafkaService() {
            return new KafkaService();
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        public ModelTrainingService modelTrainingService() {
            return Mockito.mock(ModelTrainingService.class);
        }

        @Bean
        public WebSocketService webSocketService() {
            return Mockito.mock(WebSocketService.class);
        }

        @Bean
        public ModelQueryService modelQueryService() {
            return Mockito.mock(ModelQueryService.class);
        }
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelTrainingService modelTrainingService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private ModelQueryService modelQueryService;

    @Test
    public void testKafkaService() throws InterruptedException, JsonProcessingException {
        ModelInitData modelInitData = new ModelInitData("123", 2, 0.0015f, 64, System.currentTimeMillis(), 10, 1000);
        String message = objectMapper.writeValueAsString(modelInitData);
        kafkaTemplate.send("topic_model_init", message);
        // Sleep to allow listener to process
        Thread.sleep(10000);

        // Verify that mock service received the call
        ArgumentCaptor<ModelInitData> captor = ArgumentCaptor.forClass(ModelInitData.class);
        Mockito.verify(modelTrainingService, Mockito.atLeastOnce()).receiveModelInitData(captor.capture());
        Assertions.assertEquals(captor.getValue().getLearningRate(), modelInitData.getLearningRate());
        System.out.println("_____TESTING_____");
        System.out.println(captor.getValue().toString());

        Mockito.verify(webSocketService, Mockito.atLeastOnce()).notifyModelInitData(Mockito.any());
        Mockito.verify(modelQueryService, Mockito.atLeastOnce()).getModelTrainingSummaryData(Mockito.eq(0));
    }
}

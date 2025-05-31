package com.lamt2.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.configuration.rabbitmq.RabbitMQConfiguration;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import com.lamt2.orchestrator.service.rabbitmq.RabbitMQService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class RabbitMQServiceTest {

    @Autowired
    private ModelTrainingService modelTrainingService;

    @Test
    public void testRabbitMQService() throws InterruptedException, JsonProcessingException {
        JobParameter jobParameter = new JobParameter(5, 64, 0.0001f, 10);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(jobParameter);

        modelTrainingService.createNewJob(jobParameter);

        Thread.sleep(15000);
    }
}

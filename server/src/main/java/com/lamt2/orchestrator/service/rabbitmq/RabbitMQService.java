package com.lamt2.orchestrator.service.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.configuration.rabbitmq.RabbitMQConfiguration;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RabbitMQService {
    private RabbitTemplate rabbitTemplate;

    public void sendJob(JobParameter jobParameter) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(jobParameter);
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_NAME, RabbitMQConfiguration.ROUTING_KEY, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

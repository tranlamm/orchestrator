package com.lamt2.orchestrator.service.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.configuration.rabbitmq.RabbitMQConfiguration;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    @Value("${spring.rabbitmq.exchange_name}")
    public String EXCHANGE_NAME;
    @Value("${spring.rabbitmq.routing_key}")
    public String ROUTING_KEY;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendJob(JobParameter jobParameter) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(jobParameter);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

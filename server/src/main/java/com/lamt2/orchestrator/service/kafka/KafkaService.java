package com.lamt2.orchestrator.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.model.kafka.ModelInitData;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelTrainingService modelTrainingService;

    @KafkaListener(topics = "${spring.kafka.topic_init}", groupId = "topic_model_init")
    public void listenModelInit(String message) throws JsonProcessingException {
        ModelInitData modelInitData = objectMapper.readValue(message, ModelInitData.class);
        modelTrainingService.receiveModelInitData(modelInitData);
    }

    @KafkaListener(topics = "${spring.kafka.topic_training}", groupId = "topic_model_train")
    public void listenModelTraining(String message) {
        System.out.println("Received message: " + message);
        // Handle the message (e.g., call service, save to DB)
    }

    @KafkaListener(topics = "${spring.kafka.topic_validation}", groupId = "topic_model_validate")
    public void listenModelValidate(String message) {
        System.out.println("Received message: " + message);
        // Handle the message (e.g., call service, save to DB)
    }

    @KafkaListener(topics = "${spring.kafka.topic_end}", groupId = "topic_model_end")
    public void listenModelEnd(String message) {
        System.out.println("Received message: " + message);
        // Handle the message (e.g., call service, save to DB)
    }
}

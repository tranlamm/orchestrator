package com.lamt2.orchestrator.service.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaService {
    @KafkaListener(topics = "${spring.kafka.topic_init}", groupId = "topic_model_init")
    public void listenModelInit(String message) {
        System.out.println("Received message: " + message);
        // Handle the message (e.g., call service, save to DB)
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

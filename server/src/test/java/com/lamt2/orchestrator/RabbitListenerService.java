package com.lamt2.orchestrator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class RabbitListenerService {

    @RabbitListener(queues = "JOB_TRAINING_QUEUE")
    public void receiveMessage(String message) {
        System.out.println("Received: " + message);
    }
}

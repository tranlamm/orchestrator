package com.lamt2.orchestrator.service.websocket;

import com.lamt2.orchestrator.model.kafka.ModelEndData;
import com.lamt2.orchestrator.model.kafka.ModelTrainingData;
import com.lamt2.orchestrator.model.kafka.ModelValidationData;
import com.lamt2.orchestrator.response.PaginatedModelTrainingSummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void notifyModelInitData(PaginatedModelTrainingSummaryResponse paginatedModelTrainingSummaryResponse) {
        simpMessagingTemplate.convertAndSend("/topic/model_init_data", paginatedModelTrainingSummaryResponse);
    }

    public void notifyModelTrainingData(ModelTrainingData modelTrainingData) {
        simpMessagingTemplate.convertAndSend("/topic/model_training_data", modelTrainingData);
    }

    public void notifyModelValidationData(ModelValidationData modelValidationData) {
        simpMessagingTemplate.convertAndSend("/topic/model_validation_data", modelValidationData);
    }

    public void notifyModelEndData(ModelEndData modelEndData) {
        simpMessagingTemplate.convertAndSend("/topic/model_end_data", modelEndData);
    }
}

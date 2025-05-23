package com.lamt2.orchestrator.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamt2.orchestrator.model.kafka.ModelEndData;
import com.lamt2.orchestrator.model.kafka.ModelInitData;
import com.lamt2.orchestrator.model.kafka.ModelTrainingData;
import com.lamt2.orchestrator.model.kafka.ModelValidationData;
import com.lamt2.orchestrator.service.model_query.ModelQueryService;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import com.lamt2.orchestrator.service.websocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelTrainingService modelTrainingService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private ModelQueryService modelQueryService;

    @KafkaListener(topics = "${spring.kafka.topic_init}", groupId = "topic_model_init")
    public void listenModelInit(String message) throws JsonProcessingException {
        ModelInitData modelInitData = objectMapper.readValue(message, ModelInitData.class);
        modelTrainingService.receiveModelInitData(modelInitData);
        webSocketService.notifyModelInitData(modelQueryService.getModelTrainingSummaryData(0));
    }

    @KafkaListener(topics = "${spring.kafka.topic_training}", groupId = "topic_model_train")
    public void listenModelTraining(String message) throws JsonProcessingException {
        ModelTrainingData modelTrainingData = objectMapper.readValue(message, ModelTrainingData.class);
        modelTrainingService.receiveModelTrainingData(modelTrainingData);
        webSocketService.notifyModelTrainingData(modelTrainingData);
    }

    @KafkaListener(topics = "${spring.kafka.topic_validation}", groupId = "topic_model_validate")
    public void listenModelValidate(String message) throws JsonProcessingException {
        ModelValidationData modelValidationData = objectMapper.readValue(message, ModelValidationData.class);
        modelTrainingService.receiveModelValidationData(modelValidationData);
        webSocketService.notifyModelValidationData(modelValidationData);
    }

    @KafkaListener(topics = "${spring.kafka.topic_end}", groupId = "topic_model_end")
    public void listenModelEnd(String message) throws JsonProcessingException {
        ModelEndData modelEndData = objectMapper.readValue(message, ModelEndData.class);
        modelTrainingService.receiveModelEndData(modelEndData);
        webSocketService.notifyModelEndData(modelEndData);
    }
}

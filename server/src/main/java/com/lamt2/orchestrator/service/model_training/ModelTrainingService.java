package com.lamt2.orchestrator.service.model_training;

import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.service.rabbitmq.RabbitMQService;
import com.lamt2.orchestrator.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelTrainingService {

    @Autowired
    RabbitMQService rabbitMQService;

    public void createNewJob(JobParameter jobParameter) {
        String modelId = RandomUtils.getRandomModelId();
        jobParameter.setModelId(modelId);
        rabbitMQService.sendJob(jobParameter);
    }
}

package com.lamt2.orchestrator.service.model_training;

import com.lamt2.orchestrator.configuration.redis.RedisConfiguration;
import com.lamt2.orchestrator.model.kafka.ModelInitData;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.service.rabbitmq.RabbitMQService;
import com.lamt2.orchestrator.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelTrainingService {

    @Autowired
    RabbitMQService rabbitMQService;

    @Autowired
    RedisTemplate redisTemplate;

    public void createNewJob(JobParameter jobParameter) {
        String modelId = RandomUtils.getRandomModelId();
        jobParameter.setModelId(modelId);
        rabbitMQService.sendJob(jobParameter);
    }

    public void saveModelInitData(ModelInitData modelInitData) {
        String modelId = modelInitData.getModelId();
        boolean isExisted = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(RedisConfiguration.KEY_MODEL_RUNNING_SET, modelId));
        if (!isExisted) {
            redisTemplate.opsForSet().add(RedisConfiguration.KEY_MODEL_RUNNING_SET, modelId);
        }
    }
}

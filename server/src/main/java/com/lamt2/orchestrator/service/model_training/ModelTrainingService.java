package com.lamt2.orchestrator.service.model_training;

import com.lamt2.orchestrator.configuration.redis.RedisConfiguration;
import com.lamt2.orchestrator.constant.ConstantValue;
import com.lamt2.orchestrator.model.kafka.ModelEndData;
import com.lamt2.orchestrator.model.kafka.ModelInitData;
import com.lamt2.orchestrator.model.kafka.ModelTrainingData;
import com.lamt2.orchestrator.model.kafka.ModelValidationData;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.service.rabbitmq.RabbitMQService;
import com.lamt2.orchestrator.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class ModelTrainingService {

    @Autowired
    RabbitMQService rabbitMQService;

    @Autowired
    StringRedisTemplate redisTemplate;

    public void createNewJob(JobParameter jobParameter) {
        String modelId = RandomUtils.getRandomModelId();
        jobParameter.setModelId(modelId);
        rabbitMQService.sendJob(jobParameter);
    }

    public static String getKeyModelParam(String modelId) {
        return RedisConfiguration.KEY_PREFIX_MODEL_RUNNING_PARAM + "_" + modelId;
    }

    public static String getKeyModelInfo(String modelId) {
        return RedisConfiguration.KEY_PREFIX_MODEL_RUNNING_INFO + "_" + modelId;
    }

    public static String getKeyModelTrainingData(String modelId, int epochIdx, int batchIdx) {
        return RedisConfiguration.KEY_PREFIX_MODEL_RUNNING_TRAIN + "_" + modelId + "_" + epochIdx + "_" + batchIdx;
    }

    public static String getKeyModelValidationData(String modelId, int epochIdx) {
        return RedisConfiguration.KEY_PREFIX_MODEL_RUNNING_VALIDATION + "_" + modelId + "_" + epochIdx;
    }

    public void createModelParam(ModelInitData modelInitData) {
        Map<String, String> mapModelParam = modelInitData.getMapModelParam();
        String key = ModelTrainingService.getKeyModelParam(modelInitData.getModelId());
        redisTemplate.opsForHash().putAll(key, mapModelParam);
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public void createModelInfo(ModelInitData modelInitData) {
        Map<String, String> mapModelInfo = new HashMap<>();
        mapModelInfo.put("modelId", modelInitData.getModelId());
        mapModelInfo.put("startTime", String.valueOf(modelInitData.getStartTime()));
        mapModelInfo.put("currentEpochIdx", String.valueOf(0));
        mapModelInfo.put("currentBatchIdx", String.valueOf(0));
        String key = ModelTrainingService.getKeyModelInfo(modelInitData.getModelId());
        redisTemplate.opsForHash().putAll(key, mapModelInfo);
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public void createModelTrainingData(ModelTrainingData modelTrainingData) {
        Map<String, String> mapModelInfo = modelTrainingData.getMapModelData();
        String key = ModelTrainingService.getKeyModelTrainingData(modelTrainingData.getModelId(), modelTrainingData.getEpochIdx(), modelTrainingData.getBatchIdx());
        redisTemplate.opsForHash().putAll(key, mapModelInfo);
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public void createModelValidationData(ModelValidationData modelValidationData) {
        Map<String, String> mapModelInfo = modelValidationData.getMapModelData();
        String key = ModelTrainingService.getKeyModelValidationData(modelValidationData.getModelId(), modelValidationData.getEpochIdx());
        redisTemplate.opsForHash().putAll(key, mapModelInfo);
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public void receiveModelInitData(ModelInitData modelInitData) {
        String modelId = modelInitData.getModelId();
        boolean isExisted = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(RedisConfiguration.KEY_MODEL_RUNNING_SET, modelId));
        if (isExisted) return;
        redisTemplate.opsForSet().add(RedisConfiguration.KEY_MODEL_RUNNING_SET, modelId);
        this.createModelParam(modelInitData);
        this.createModelInfo(modelInitData);
    }

    public void receiveModelTrainingData(ModelTrainingData modelTrainingData) {
        String modelId = modelTrainingData.getModelId();
        String keyInfo = ModelTrainingService.getKeyModelInfo(modelId);
        int oldEpochIdx = Integer.parseInt((String) Optional.ofNullable(redisTemplate.opsForHash().get(keyInfo, "currentEpochIdx")).orElse(ConstantValue.STRING_ZERO));
        int oldBatchIdx = Integer.parseInt((String) Optional.ofNullable(redisTemplate.opsForHash().get(keyInfo, "currentBatchIdx")).orElse(ConstantValue.STRING_ZERO));
        if (modelTrainingData.getEpochIdx() > oldEpochIdx) {
            redisTemplate.opsForHash().put(keyInfo, "currentEpochIdx", String.valueOf(modelTrainingData.getEpochIdx()));
        }
        if (modelTrainingData.getBatchIdx() > oldBatchIdx) {
            redisTemplate.opsForHash().put(keyInfo, "currentBatchIdx", String.valueOf(modelTrainingData.getBatchIdx()));
        }
        this.createModelTrainingData(modelTrainingData);
    }

    public void receiveModelValidationData(ModelValidationData modelValidationData) {
        this.createModelValidationData(modelValidationData);
    }

    public void receiveModelEndData(ModelEndData modelEndData) {

    }
}

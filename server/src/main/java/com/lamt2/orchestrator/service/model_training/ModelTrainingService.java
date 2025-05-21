package com.lamt2.orchestrator.service.model_training;

import com.lamt2.orchestrator.configuration.redis.RedisConfiguration;
import com.lamt2.orchestrator.constant.ConstantValue;
import com.lamt2.orchestrator.entity.*;
import com.lamt2.orchestrator.model.kafka.ModelEndData;
import com.lamt2.orchestrator.model.kafka.ModelInitData;
import com.lamt2.orchestrator.model.kafka.ModelTrainingData;
import com.lamt2.orchestrator.model.kafka.ModelValidationData;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.repository.ModelResultRepository;
import com.lamt2.orchestrator.service.rabbitmq.RabbitMQService;
import com.lamt2.orchestrator.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelTrainingService {

    @Autowired
    RabbitMQService rabbitMQService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ModelResultRepository modelResultRepository;

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

    public Map<String, String> getRedisMapData(String key) {
        if (!redisTemplate.hasKey(key)) return null;
        Map<Object, Object> mapObject = redisTemplate.opsForHash().entries(key);
        return mapObject.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
    }

    public Map<String, String> getModelParam(String modelId) {
        String key = ModelTrainingService.getKeyModelParam(modelId);
        return getRedisMapData(key);
    }

    public Map<String, String> getModelInfo(String modelId) {
        String key = ModelTrainingService.getKeyModelInfo(modelId);
        return getRedisMapData(key);
    }

    public Map<String, String> getModelValidationData(String modelId, int epochIdx) {
        String key = ModelTrainingService.getKeyModelValidationData(modelId, epochIdx);
        return getRedisMapData(key);
    }

    public Map<String, String> getModelTrainingData(String modelId, int epochIdx, int batchIdx) {
        String key = ModelTrainingService.getKeyModelTrainingData(modelId, epochIdx, batchIdx);
        return getRedisMapData(key);
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
        String modelId = modelEndData.getModelId();
        Map<String, String> modelParam = getModelParam(modelId);
        Map<String, String> modelInfo = getModelInfo(modelId);
        if (modelParam == null || modelInfo == null) return;
        ModelResult modelResult = new ModelResult();
        modelResult.setModelId(modelId);
        modelResult.setStartTime(Long.parseLong(modelInfo.get("startTime")));
        modelResult.setEndTime(modelEndData.getEndTime());

        ModelParam params = new ModelParam();
        params.setBatchSize(Integer.parseInt(modelParam.get("batchSize")));
        params.setNumEpoch(Integer.parseInt(modelParam.get("numEpoch")));
        params.setLearningRate(Float.parseFloat(modelParam.get("learningRate")));
        int numBatchPerEpoch = Integer.parseInt(modelParam.get("totalBatch"));
        params.setNumBatchPerEpoch(numBatchPerEpoch);
        modelResult.setParam(params);
        int logInterval = Integer.parseInt(modelParam.get("logInterval"));
        modelResult.setLogInterval(logInterval);

        ModelFinalInfo modelFinalInfo = new ModelFinalInfo();
        modelFinalInfo.setAccuracy(modelEndData.getAccuracy());
        modelFinalInfo.setLoss(modelEndData.getLoss());
        modelFinalInfo.setF1Score(modelEndData.getF1Score());
        modelResult.setFinalResult(modelFinalInfo);

        List<ModelTrainingInfo> modelTrainingInfoList = new ArrayList<>();
        List<ModelValidationInfo> modelValidationInfoList = new ArrayList<>();

        int numEpoch = modelResult.getParam().getNumEpoch();
        for (int i = 0; i < numEpoch; ++i) {
            int epochIdx = i + 1;
            for (int j = logInterval; j <= numBatchPerEpoch; j = j + logInterval) {
                Map<String, String> trainingData = getModelTrainingData(modelId, epochIdx, j);
                if (trainingData == null) continue;
                ModelTrainingInfo modelTrainingInfo = new ModelTrainingInfo();
                modelTrainingInfo.setEpochIdx(Integer.parseInt(trainingData.get("epochIdx")));
                modelTrainingInfo.setBatchIdx(Integer.parseInt(trainingData.get("batchIdx")));
                modelTrainingInfo.setLoss(Integer.parseInt(trainingData.get("loss")));
                modelTrainingInfo.setAccuracy(Integer.parseInt(trainingData.get("accuracy")));
                modelTrainingInfoList.add(modelTrainingInfo);
            }

            Map<String, String> validationData = getModelValidationData(modelId, epochIdx);
            if (validationData == null) continue;
            ModelValidationInfo modelValidationInfo = new ModelValidationInfo();
            modelValidationInfo.setEpochIdx(Integer.parseInt(validationData.get("epochIdx")));
            modelValidationInfo.setAccuracy(Float.parseFloat(validationData.get("accuracy")));
            modelValidationInfo.setLoss(Float.parseFloat(validationData.get("loss")));
            modelValidationInfoList.add(modelValidationInfo);
        }

        modelResult.setTrainingInfo(modelTrainingInfoList);
        modelResult.setValidationInfo(modelValidationInfoList);

        modelResultRepository.save(modelResult);
        this.deleteModelFromRedis(modelId, numEpoch, numBatchPerEpoch, logInterval);
    }

    public void deleteModelFromRedis(String modelId, int numEpoch, int numBatchPerEpoch, int logInterval) {
        redisTemplate.delete(ModelTrainingService.getKeyModelInfo(modelId));
        redisTemplate.delete(ModelTrainingService.getKeyModelParam(modelId));
        for (int i = 0; i < numEpoch; ++i) {
            int epochIdx = i + 1;
            for (int j = logInterval; j <= numBatchPerEpoch; j = j + logInterval) {
                redisTemplate.delete(ModelTrainingService.getKeyModelTrainingData(modelId, epochIdx, j));
            }

            redisTemplate.delete(ModelTrainingService.getKeyModelValidationData(modelId, epochIdx));
        }
    }
}

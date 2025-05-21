package com.lamt2.orchestrator.model.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelInitData {
    @JsonProperty("model_id")
    private String modelId;

    @JsonProperty("n_epochs")
    private int numEpoch;

    @JsonProperty("learning_rate")
    private float learningRate;

    @JsonProperty("batch_size")
    private int batchSize;

    @JsonProperty("start_time")
    private long startTime;

    @JsonProperty("log_interval")
    private int logInterval;

    @JsonProperty("total_batch")
    private int totalBatch;

    public String getModelId() {
        return modelId;
    }

    public int getNumEpoch() {
        return numEpoch;
    }

    public float getLearningRate() {
        return learningRate;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getLogInterval() {
        return logInterval;
    }

    public int getTotalBatch() {
        return totalBatch;
    }

    public Map<String, String> getMapModelParam() {
        Map<String, String> mapModelParam = new HashMap<>();
        mapModelParam.put("modelId", modelId);
        mapModelParam.put("numEpoch", String.valueOf(numEpoch));
        mapModelParam.put("learningRate", String.valueOf(learningRate));
        mapModelParam.put("batchSize", String.valueOf(batchSize));
        mapModelParam.put("logInterval", String.valueOf(logInterval));
        mapModelParam.put("totalBatch", String.valueOf(totalBatch));
        return mapModelParam;
    }
}

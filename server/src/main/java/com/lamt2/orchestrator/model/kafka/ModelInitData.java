package com.lamt2.orchestrator.model.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}

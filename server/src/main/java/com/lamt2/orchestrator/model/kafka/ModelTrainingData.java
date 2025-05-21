package com.lamt2.orchestrator.model.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelTrainingData {
    @JsonProperty("model_id")
    private String modelId;

    @JsonProperty("epoch_idx")
    private int epochIdx;

    @JsonProperty("batch_idx")
    private int batchIdx;

    @JsonProperty("train_acc")
    private float accuracy;

    @JsonProperty("train_loss")
    private float loss;

    public String getModelId() {
        return modelId;
    }

    public int getEpochIdx() {
        return epochIdx;
    }

    public int getBatchIdx() {
        return batchIdx;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getLoss() {
        return loss;
    }
}

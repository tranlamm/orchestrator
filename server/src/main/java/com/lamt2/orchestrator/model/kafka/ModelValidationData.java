package com.lamt2.orchestrator.model.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelValidationData {
    @JsonProperty("model_id")
    private String modelId;

    @JsonProperty("epoch_idx")
    private int epochIdx;

    @JsonProperty("val_acc")
    private float accuracy;

    @JsonProperty("val_loss")
    private float loss;

    public String getModelId() {
        return modelId;
    }

    public int getEpochIdx() {
        return epochIdx;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getLoss() {
        return loss;
    }
}

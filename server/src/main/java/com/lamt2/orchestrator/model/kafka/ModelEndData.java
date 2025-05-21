package com.lamt2.orchestrator.model.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelEndData {
    @JsonProperty("model_id")
    private String modelId;

    @JsonProperty("end_time")
    private long endTime;

    @JsonProperty("f1_score")
    private int f1Score;

    @JsonProperty("test_acc")
    private float accuracy;

    @JsonProperty("test_loss")
    private float loss;

    public String getModelId() {
        return modelId;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getF1Score() {
        return f1Score;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getLoss() {
        return loss;
    }
}

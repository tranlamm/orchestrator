package com.lamt2.orchestrator.model.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, String> getMapModelData() {
        Map<String, String> mapModelData = new HashMap<>();
        mapModelData.put("modelId", modelId);
        mapModelData.put("epochIdx", String.valueOf(epochIdx));
        mapModelData.put("batchIdx", String.valueOf(batchIdx));
        mapModelData.put("accuracy", String.valueOf(accuracy));
        mapModelData.put("loss", String.valueOf(loss));
        return mapModelData;
    }
}

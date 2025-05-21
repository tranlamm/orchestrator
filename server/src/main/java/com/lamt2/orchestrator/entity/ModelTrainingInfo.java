package com.lamt2.orchestrator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelTrainingInfo {
    @Field("epoch_idx")
    private int epochIdx;

    @Field("batch_idx")
    private int batchIdx;

    private float accuracy;

    private float loss;

    public ModelTrainingInfo() {
    }

    public int getEpochIdx() {
        return epochIdx;
    }

    public void setEpochIdx(int epochIdx) {
        this.epochIdx = epochIdx;
    }

    public int getBatchIdx() {
        return batchIdx;
    }

    public void setBatchIdx(int batchIdx) {
        this.batchIdx = batchIdx;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getLoss() {
        return loss;
    }

    public void setLoss(float loss) {
        this.loss = loss;
    }
}

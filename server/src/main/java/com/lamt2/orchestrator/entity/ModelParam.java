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
public class ModelParam {
    @Field("num_epoch")
    private int numEpoch;

    @Field("learning_rate")
    private float learningRate;

    @Field("batch_size")
    private int batchSize;

    @Field("num_batch_per_epoch")
    private int numBatchPerEpoch;

    public int getNumEpoch() {
        return numEpoch;
    }

    public void setNumEpoch(int numEpoch) {
        this.numEpoch = numEpoch;
    }

    public float getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(float learningRate) {
        this.learningRate = learningRate;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getNumBatchPerEpoch() {
        return numBatchPerEpoch;
    }

    public void setNumBatchPerEpoch(int numBatchPerEpoch) {
        this.numBatchPerEpoch = numBatchPerEpoch;
    }
}

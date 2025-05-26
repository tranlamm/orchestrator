package com.lamt2.orchestrator.model.rabbitmq;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobParameter {
    @NotNull(message = "Epoch is required")
    private Integer epochs;
    @NotNull(message = "Batchsize is required")
    private Integer batchSize;
    @NotNull(message = "Learning rate is required")
    private Float learningRate;
    @NotNull(message = "Log intervals is required")
    private Integer logIntervals;

    private String modelId;

    public JobParameter(int epochs, int batchSize, float learningRate, int logIntervals) {
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.learningRate = learningRate;
        this.logIntervals = logIntervals;
    }

    public int getEpochs() {
        return epochs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public float getLearningRate() {
        return learningRate;
    }

    public int getLogIntervals() {
        return logIntervals;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setLearningRate(float learningRate) {
        this.learningRate = learningRate;
    }

    public void setLogIntervals(int logIntervals) {
        this.logIntervals = logIntervals;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}

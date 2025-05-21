package com.lamt2.orchestrator.response;

import com.lamt2.orchestrator.utils.MathUtils;

public class ModelTrainingSummaryResponse {
    private String modelId;
    private long startTime;
    private int currentEpochIdx;
    private int currentBatchIdx;
    private int totalEpoch;
    private int numBatchPerEpoch;
    private float currentAccuracy;
    private float currentLoss;
    private float progress;

    public ModelTrainingSummaryResponse(String modelId, long startTime, int currentEpochIdx, int currentBatchIdx, int totalEpoch, int numBatchPerEpoch, float currentAccuracy, float currentLoss) {
        this.modelId = modelId;
        this.startTime = startTime;
        this.currentEpochIdx = currentEpochIdx;
        this.currentBatchIdx = currentBatchIdx;
        this.totalEpoch = totalEpoch;
        this.numBatchPerEpoch = numBatchPerEpoch;
        this.currentAccuracy = currentAccuracy;
        this.currentLoss = currentLoss;
        int totalBatch = this.totalEpoch * this.numBatchPerEpoch;
        int numBatchDone = Math.max(this.currentEpochIdx - 1, 0) * this.numBatchPerEpoch + this.currentBatchIdx;
        this.progress = MathUtils.roundFloat((float) numBatchDone / totalBatch);
    }

    public String getModelId() {
        return modelId;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getCurrentEpochIdx() {
        return currentEpochIdx;
    }

    public int getCurrentBatchIdx() {
        return currentBatchIdx;
    }

    public int getTotalEpoch() {
        return totalEpoch;
    }

    public int getNumBatchPerEpoch() {
        return numBatchPerEpoch;
    }

    public float getCurrentAccuracy() {
        return currentAccuracy;
    }

    public float getCurrentLoss() {
        return currentLoss;
    }

    public float getProgress() {
        return progress;
    }
}

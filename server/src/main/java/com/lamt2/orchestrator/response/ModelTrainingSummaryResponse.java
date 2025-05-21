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
}

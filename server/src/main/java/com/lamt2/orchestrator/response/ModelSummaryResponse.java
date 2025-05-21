package com.lamt2.orchestrator.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelSummaryResponse {
    private String modelId;

    private long startTime;

    private long endTime;

    private float accuracy;

    private float loss;

    private float f1Score;

    private long duration;

    public ModelSummaryResponse(String modelId, long startTime, long endTime, float accuracy, float loss, float f1Score, long duration) {
        this.modelId = modelId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.accuracy = accuracy;
        this.loss = loss;
        this.f1Score = f1Score;
        this.duration = duration;
    }
}

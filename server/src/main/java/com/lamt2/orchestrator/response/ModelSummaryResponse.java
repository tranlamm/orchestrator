package com.lamt2.orchestrator.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Unique identifier model id", example = "001")
    private String modelId;

    private long startTime;

    private long endTime;

    private float accuracy;

    private float loss;

    private float f1Score;

    private long duration;
}

package com.lamt2.orchestrator.response;

import com.lamt2.orchestrator.entity.ModelParam;
import com.lamt2.orchestrator.entity.ModelTrainingInfo;
import com.lamt2.orchestrator.entity.ModelValidationInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModelDetailResponse {
    @Schema(description = "Unique identifier model id", example = "001")
    private String modelId;

    private ModelParam param;

    private int logInterval;

    private List<ModelTrainingInfo> trainingInfo;

    private List<ModelValidationInfo> validationInfo;
}

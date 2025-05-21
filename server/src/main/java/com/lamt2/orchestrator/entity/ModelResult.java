package com.lamt2.orchestrator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "model_result")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelResult {
    @Id
    private String id;

    @Indexed(unique = true)
    @Field("model_id")
    private String modelId;

    @Field("start_time")
    private long startTime;

    @Field("end_time")
    private long endTime;

    private ModelParam param;

    @Field("log_interval")
    private int logInterval;

    @Field("training_info")
    private List<ModelTrainingInfo> trainingInfo;

    @Field("validation_info")
    private List<ModelValidationInfo> validationInfo;

    @Field("final_result")
    private ModelFinalInfo finalResult;

    public String getId() {
        return id;
    }

    public String getModelId() {
        return modelId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public ModelParam getParam() {
        return param;
    }

    public int getLogInterval() {
        return logInterval;
    }

    public List<ModelTrainingInfo> getTrainingInfo() {
        return trainingInfo;
    }

    public List<ModelValidationInfo> getValidationInfo() {
        return validationInfo;
    }

    public ModelFinalInfo getFinalResult() {
        return finalResult;
    }
}

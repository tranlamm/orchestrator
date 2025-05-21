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
public class ModelValidationInfo {
    @Field("epoch_idx")
    private int epochIdx;

    private float accuracy;

    private float loss;

    public int getEpochIdx() {
        return epochIdx;
    }

    public void setEpochIdx(int epochIdx) {
        this.epochIdx = epochIdx;
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

    public ModelValidationInfo() {
    }
}

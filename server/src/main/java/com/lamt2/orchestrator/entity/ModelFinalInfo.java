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
public class ModelFinalInfo {
    private float accuracy;

    private float loss;

    @Field("f1_score")
    private float f1Score;

    public float getAccuracy() {
        return accuracy;
    }

    public float getLoss() {
        return loss;
    }

    public float getF1Score() {
        return f1Score;
    }

    public ModelFinalInfo() {
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setLoss(float loss) {
        this.loss = loss;
    }

    public void setF1Score(float f1Score) {
        this.f1Score = f1Score;
    }
}

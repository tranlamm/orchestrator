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
public class ModelTrainingInfo {
    @Field("epoch_idx")
    private int epochIdx;

    @Field("batch_idx")
    private int batchIdx;

    private float accuracy;

    private float loss;
}

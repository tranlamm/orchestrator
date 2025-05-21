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
public class ModelParam {
    @Field("num_epoch")
    private int numEpoch;

    @Field("learning_rate")
    private float learningRate;

    @Field("batch_size")
    private int batchSize;

    @Field("num_batch_per_epoch")
    private int numBatchPerEpoch;
}

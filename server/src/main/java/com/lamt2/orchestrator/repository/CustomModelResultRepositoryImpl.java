package com.lamt2.orchestrator.repository;

import com.lamt2.orchestrator.exception.UnsortableFieldException;
import com.lamt2.orchestrator.response.ModelSummaryResponse;
import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class CustomModelResultRepositoryImpl implements CustomModelResultRepository{
    @Value("${spring.mongodb.model_result.page_size}")
    private int size;

    private static final List<String> LIST_SORTABLE = Arrays.asList("duration", "f1Score", "loss", "accuracy", "startTime", "endTime");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PaginatedModelSummaryResponse findAllSortedByDuration(int page, boolean ascending, String sortedField) {
        if (!LIST_SORTABLE.contains(sortedField)) {
            throw new UnsortableFieldException(String.format("Field %s un sortable!", sortedField));
        }
        ProjectionOperation projection = Aggregation.project("model_id", "start_time", "end_time",
                        "final_result.accuracy", "final_result.loss", "final_result.f1_score")
                .and("model_id").as("modelId")
                .and("start_time").as("startTime")
                .and("end_time").as("endTime")
                .and("final_result.accuracy").as("accuracy")
                .and("final_result.loss").as("loss")
                .and("f1Score").as("f1Score")
                .andExpression("end_time - start_time").as("duration");

        AggregationOperation aggregationSort = Aggregation.sort(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortedField);
        AggregationOperation aggregationSkip = Aggregation.skip((long) page * size);
        AggregationOperation aggregationLimit = Aggregation.limit(size);

        Aggregation aggregation = Aggregation.newAggregation(
                projection,
                aggregationSort,
                aggregationSkip,
                aggregationLimit
        );

        AggregationResults<ModelSummaryResponse> result = mongoTemplate.aggregate(aggregation, "model_result", ModelSummaryResponse.class);
        List<ModelSummaryResponse> content = result.getMappedResults();

        long totalCount = mongoTemplate.count(new Query(), "model_result");

        return new PaginatedModelSummaryResponse(content, page, (int) Math.ceil((double) totalCount / size), size);
    }
}

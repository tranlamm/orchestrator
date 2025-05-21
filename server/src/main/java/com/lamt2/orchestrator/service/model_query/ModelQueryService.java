package com.lamt2.orchestrator.service.model_query;

import com.lamt2.orchestrator.entity.ModelResult;
import com.lamt2.orchestrator.exception.UnsortableFieldException;
import com.lamt2.orchestrator.repository.ModelResultRepository;
import com.lamt2.orchestrator.response.ModelDetailResponse;
import com.lamt2.orchestrator.response.ModelSummaryResponse;
import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;
import com.lamt2.orchestrator.response.PaginatedModelTrainingSummaryResponse;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelQueryService {
    @Value("${spring.mongodb.model_result.page_size}")
    private int defaultPageSize;

    @Value("${spring.redis.model_training.page_size")
    private int defaultTrainingPageSize;

    private static final List<String> LIST_MODEL_TRAINING_SORTABLE_FIELD = Arrays.asList("currentLoss", "currentAccuracy", "startTime", "progress", "totalEpoch");

    @Autowired
    ModelResultRepository modelResultRepository;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public PaginatedModelSummaryResponse getModelSummaryData(int pageIdx, boolean ascending, String sortedField) {
        return modelResultRepository.findAllSortedByDuration(pageIdx, ascending, sortedField);
    }

    public PaginatedModelSummaryResponse getModelSummaryData(int pageIdx) {
        Pageable pageable = PageRequest.of(pageIdx, defaultPageSize);
        Page<ModelResult> page = modelResultRepository.findAll(pageable);
        List<ModelSummaryResponse> modelSummaryResponseList = page.getContent().stream().map(modelResult -> new ModelSummaryResponse(
                modelResult.getId(),
                modelResult.getStartTime(),
                modelResult.getEndTime(),
                modelResult.getFinalResult().getAccuracy(),
                modelResult.getFinalResult().getLoss(),
                modelResult.getFinalResult().getF1Score(),
                modelResult.getEndTime() - modelResult.getStartTime()
        )).collect(Collectors.toList());

        return new PaginatedModelSummaryResponse(modelSummaryResponseList, page.getNumber(), page.getTotalPages(), page.getSize());
    }

    public ModelDetailResponse getModelDetail(String modelId) {
        ModelResult modelResult = modelResultRepository.findByModelId(modelId);
        if (modelResult == null) {
            throw new ResourceNotFoundException("Model not found: " + modelId);
        }
        return new ModelDetailResponse(
                modelResult.getId(),
                modelResult.getParam(),
                modelResult.getLogInterval(),
                modelResult.getTrainingInfo(),
                modelResult.getValidationInfo()
        );
    }

    public PaginatedModelTrainingSummaryResponse getModelTrainingSummaryData(int pageIdx, boolean ascending, String sortedField) {
        if (!LIST_MODEL_TRAINING_SORTABLE_FIELD.contains(sortedField)) {
            throw new UnsortableFieldException(String.format("Field %s un sortable!", sortedField));
        }

    }

    public PaginatedModelTrainingSummaryResponse getModelTrainingSummaryData(int pageIdx) {
    }
}

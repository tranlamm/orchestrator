package com.lamt2.orchestrator.service.model_query;

import com.lamt2.orchestrator.entity.ModelResult;
import com.lamt2.orchestrator.exception.UnsortableFieldException;
import com.lamt2.orchestrator.repository.ModelResultRepository;
import com.lamt2.orchestrator.response.*;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    ModelTrainingService modelTrainingService;

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

        List<ModelTrainingSummaryResponse> list = modelTrainingService.getListModelTrainingSummary();
        int sortSigned = ascending ? -1 : 1;
        list.sort((a, b) -> {
            switch (sortedField) {
                case "currentLoss":
                    if (b.getCurrentLoss() == a.getCurrentLoss()) return 0;
                    return ((b.getCurrentLoss() - a.getCurrentLoss()) * sortSigned > 0) ? 1 : -1;
                case "currentAccuracy":
                    if (b.getCurrentAccuracy() == a.getCurrentAccuracy()) return 0;
                    return ((b.getCurrentAccuracy() - a.getCurrentAccuracy()) * sortSigned > 0) ? 1 : -1;
                case "startTime":
                    if (b.getStartTime() == a.getStartTime()) return 0;
                    return ((b.getStartTime() - a.getStartTime()) * sortSigned > 0) ? 1 : -1;
                case "totalEpoch":
                    if (b.getTotalEpoch() == a.getTotalEpoch()) return 0;
                    return ((b.getTotalEpoch() - a.getTotalEpoch()) * sortSigned > 0) ? 1 : -1;
                case "progress":
                    if (b.getProgress() == a.getProgress()) return 0;
                    return ((b.getProgress() - a.getProgress()) * sortSigned > 0) ? 1 : -1;
                default:
                    return 0;
            }
        });
        int total = list.size();
        int maxPage = (int) Math.ceil((float) total / defaultTrainingPageSize);
        pageIdx = Math.min(pageIdx, maxPage);
        int begin = pageIdx * defaultTrainingPageSize;
        int end = Math.min(begin + defaultTrainingPageSize, total);
        List<ModelTrainingSummaryResponse> result = list.subList(begin, end);
        return new PaginatedModelTrainingSummaryResponse(result, pageIdx, maxPage, defaultTrainingPageSize);
    }

    public PaginatedModelTrainingSummaryResponse getModelTrainingSummaryData(int pageIdx) {
        List<ModelTrainingSummaryResponse> list = modelTrainingService.getListModelTrainingSummary();
        int total = list.size();
        int maxPage = (int) Math.ceil((float) total / defaultTrainingPageSize);
        pageIdx = Math.min(pageIdx, maxPage);
        int begin = pageIdx * defaultTrainingPageSize;
        int end = Math.min(begin + defaultTrainingPageSize, total);
        List<ModelTrainingSummaryResponse> result = list.subList(begin, end);
        return new PaginatedModelTrainingSummaryResponse(result, pageIdx, maxPage, defaultTrainingPageSize);
    }

    public ModelDetailResponse getModelTrainingDetailResponse(String modelId) {
        return modelTrainingService.getModelTrainingDetailResponse(modelId);
    }
}

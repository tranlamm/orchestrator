package com.lamt2.orchestrator.controller;

import com.lamt2.orchestrator.response.ModelDetailResponse;
import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;
import com.lamt2.orchestrator.response.PaginatedModelTrainingSummaryResponse;
import com.lamt2.orchestrator.service.model_query.ModelQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/view/model")
public class ModelQueryController {
    @Autowired
    ModelQueryService modelQueryService;

    @GetMapping("/result/all")
    public ResponseEntity<PaginatedModelSummaryResponse> getAllModelSummary(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "true", required = false) boolean ascending,
            @RequestParam(required = false) String sortedField
    ) {
        if (sortedField != null) {
            return new ResponseEntity<>(modelQueryService.getModelSummaryData(page, ascending, sortedField), HttpStatus.OK);
        }

        return new ResponseEntity<>(modelQueryService.getModelSummaryData(page), HttpStatus.OK);
    }

    @GetMapping("/result/{modelId}")
    public ResponseEntity<ModelDetailResponse> getModelDetail(
            @PathVariable String modelId
    ) {
        return new ResponseEntity<>(modelQueryService.getModelDetail(modelId), HttpStatus.OK);
    }

    @GetMapping("/training/all")
    public ResponseEntity<PaginatedModelTrainingSummaryResponse> getAllModelTrainingSummary(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "true", required = false) boolean ascending,
            @RequestParam(required = false) String sortedField
    ) {
        if (sortedField != null) {
            return new ResponseEntity<>(modelQueryService.getModelTrainingSummaryData(page, ascending, sortedField), HttpStatus.OK);
        }

        return new ResponseEntity<>(modelQueryService.getModelTrainingSummaryData(page), HttpStatus.OK);
    }

    @GetMapping("/training/{modelId}")
    public ResponseEntity<ModelDetailResponse> getModelTrainingDetail(
            @PathVariable String modelId
    ) {
        return new ResponseEntity<>(modelQueryService.getModelTrainingDetailResponse(modelId), HttpStatus.OK);
    }
}

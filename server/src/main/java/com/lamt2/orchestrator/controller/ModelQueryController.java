package com.lamt2.orchestrator.controller;

import com.lamt2.orchestrator.response.ModelDetailResponse;
import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;
import com.lamt2.orchestrator.response.PaginatedModelTrainingSummaryResponse;
import com.lamt2.orchestrator.service.model_query.ModelQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User model api", description = "Operation to query model result")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/view/model")
public class ModelQueryController {
    @Autowired
    ModelQueryService modelQueryService;

    @Operation(
            summary = "Get all model finished result",
            description = "Get all model finished result with paginated, sortable field"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Model result found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PaginatedModelSummaryResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Unsupported sort field"
    )
    @GetMapping("/result/all")
    public ResponseEntity<PaginatedModelSummaryResponse> getAllModelSummary(
            @Parameter(
                    name = "page index",
                    description = "index of page (start from 0)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0", required = false) int page,
            @Parameter(
                    name = "order",
                    description = "sort order (true is ascending, false is descending)",
                    example = "true"
            )
            @RequestParam(defaultValue = "true", required = false) boolean ascending,
            @Parameter(
                    name = "sorted field",
                    description = "field to sort (\"duration\", \"f1Score\", \"loss\", \"accuracy\", \"startTime\", \"endTime\")",
                    example = "duration"
            )
            @RequestParam(required = false) String sortedField
    ) {
        if (sortedField != null) {
            return new ResponseEntity<>(modelQueryService.getModelSummaryData(page, ascending, sortedField), HttpStatus.OK);
        }

        return new ResponseEntity<>(modelQueryService.getModelSummaryData(page), HttpStatus.OK);
    }

    @Operation(
            summary = "Get model detail result",
            description = "Get model detail result include: model param, model training result of each epoch, batch"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Model detail result found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModelDetailResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Model not found"
    )
    @GetMapping("/result/{modelId}")
    public ResponseEntity<ModelDetailResponse> getModelDetail(
            @Parameter(
                    name = "model id",
                    description = "id of model (string)"
            )
            @PathVariable String modelId
    ) {
        return new ResponseEntity<>(modelQueryService.getModelDetail(modelId), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all current training model summary",
            description = "Get all current training model summary with paginated, sortable"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Model training result found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PaginatedModelTrainingSummaryResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Model training result not found"
    )
    @GetMapping("/training/all")
    public ResponseEntity<PaginatedModelTrainingSummaryResponse> getAllModelTrainingSummary(
            @Parameter(
                    name = "page idx",
                    description = "index of page (start from 0)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0", required = false) int page,
            @Parameter(
                    name = "order",
                    description = "sort order (true is ascending, false is descending)",
                    example = "true"
            )
            @RequestParam(defaultValue = "true", required = false) boolean ascending,
            @Parameter(
                    name = "sorted field",
                    description = "field to sort (\"currentLoss\", \"currentAccuracy\", \"startTime\", \"progress\", \"totalEpoch\")",
                    example = "progress"
            )
            @RequestParam(required = false) String sortedField
    ) {
        if (sortedField != null) {
            return new ResponseEntity<>(modelQueryService.getModelTrainingSummaryData(page, ascending, sortedField), HttpStatus.OK);
        }

        return new ResponseEntity<>(modelQueryService.getModelTrainingSummaryData(page), HttpStatus.OK);
    }

    @Operation(
            summary = "Get model training result detail",
            description = "Get model training result detail with model param, result of each epoch & batch"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Model training result detail found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModelDetailResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Model training result detail not found"
    )
    @GetMapping("/training/{modelId}")
    public ResponseEntity<ModelDetailResponse> getModelTrainingDetail(
            @Parameter(
                    name = "model id",
                    description = "id of model"
            )
            @PathVariable String modelId
    ) {
        return new ResponseEntity<>(modelQueryService.getModelTrainingDetailResponse(modelId), HttpStatus.OK);
    }
}

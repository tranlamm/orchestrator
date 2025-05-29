package com.lamt2.orchestrator.controller;

import com.lamt2.orchestrator.exception.MissingFieldException;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.response.BaseResponse;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Admin model api", description = "Operation to create new job")
@RestController
@RequestMapping("/api/admin/model")
@SecurityRequirement(name = "bearerAuth")
public class ModelJobController {
    @Autowired
    ModelTrainingService modelTrainingService;

    @Operation(
            summary = "Create new job",
            description = "Create new model training job, need admin role"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Create new job success!"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Missing field in request body"
    )
    @PostMapping(value = "/new_job", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> createNewJob(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Job parameter of new model training job",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JobParameter.class)
                    )
            )
            @Valid @RequestBody JobParameter jobParameter,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            throw new MissingFieldException(errors);
        }

        modelTrainingService.createNewJob(jobParameter);
        Map<String, String> params = new HashMap<>();
        params.put("message", "Send create job!");
        return new ResponseEntity<>(new BaseResponse(params), HttpStatus.OK);
    }
}

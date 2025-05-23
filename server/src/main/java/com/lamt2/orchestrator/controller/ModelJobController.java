package com.lamt2.orchestrator.controller;

import com.lamt2.orchestrator.exception.MissingFieldException;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Tag(name = "Admin model api", description = "Operation to create new job")
@Controller
@RequestMapping("/api/admin/model")
public class ModelJobController {
    @Autowired
    ModelTrainingService modelTrainingService;

    @Operation(
            summary = "Create new job",
            description = "Create new model training job, need admin role"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Create new job success!"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Missing field in request body"
    )
    @PostMapping("/new_job")
    @ResponseBody
    public ResponseEntity<String> createNewJob(
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
        return new ResponseEntity<>("Send create job!", HttpStatus.NO_CONTENT);
    }
}

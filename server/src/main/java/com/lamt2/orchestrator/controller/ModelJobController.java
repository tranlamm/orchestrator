package com.lamt2.orchestrator.controller;

import com.lamt2.orchestrator.exception.MissingFieldException;
import com.lamt2.orchestrator.model.rabbitmq.JobParameter;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import com.lamt2.orchestrator.service.rabbitmq.RabbitMQService;
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

@Controller
@RequestMapping("/api/admin/model")
public class ModelJobController {
    @Autowired
    ModelTrainingService modelTrainingService;

    @PostMapping("/new_job")
    @ResponseBody
    public ResponseEntity<String> createNewJob(@Valid @RequestBody JobParameter jobParameter, BindingResult bindingResult) {
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

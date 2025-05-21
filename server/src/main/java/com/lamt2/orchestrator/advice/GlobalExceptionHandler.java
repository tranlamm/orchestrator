package com.lamt2.orchestrator.advice;

import com.lamt2.orchestrator.exception.MissingFieldException;
import com.lamt2.orchestrator.exception.UnsortableFieldException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnsortableFieldException.class)
    public ResponseEntity<String> handleUnsortableField(UnsortableFieldException unsortableFieldException) {
        return new ResponseEntity<>(unsortableFieldException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingFieldException.class)
    public ResponseEntity<String> handleMissingField(MissingFieldException missingFieldException) {
        return new ResponseEntity<>(missingFieldException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";

        String message = String.format("Parameter '%s' must be of type %s, but got '%s'", name, type, value);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}

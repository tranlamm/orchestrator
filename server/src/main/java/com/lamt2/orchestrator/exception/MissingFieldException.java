package com.lamt2.orchestrator.exception;

public class MissingFieldException extends RuntimeException{
    public MissingFieldException(String message) {
        super(message);
    }
}

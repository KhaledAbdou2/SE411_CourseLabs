package edu.psu.se411.lab14.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DatabaseExceptionHandler {
    // Handles two concurrent POST requests racing to claim the same supplied ID.
    @ExceptionHandler(DbActionExecutionException.class)
    public ProblemDetail handleDatabaseAction(DbActionExecutionException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof DuplicateKeyException) {
                return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Course ID already exists");
            }
        }
        throw exception;
    }
}

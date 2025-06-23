package com.mindex.challenge.controller;

import com.mindex.challenge.data.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Global exception handler to return an error message and an error code in case we encounter an exception.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle IllegalStateException, which gets thrown when an employee is not found.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleEmployeeNotFound(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        var error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Employee Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Any unexpected exceptions.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        var error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.internalServerError().body(error);
    }}

package com.mindex.challenge.data.error;

import java.time.Instant;

/**
 * Object for capturing data when we encounter an exception. Used in GlobalExceptionHandler
 * @param status
 * @param error
 * @param message
 * @param path
 * @param timestamp
 */
public record ApiError(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp
){}

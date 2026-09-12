package com.acme.commerce.order.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError notFound(IllegalArgumentException ex) {
        return new ApiError("NOT_FOUND", ex.getMessage(), Instant.now(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError invalid(MethodArgumentNotValidException ex) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage(),
                        (first, second) -> first
                ));
        return new ApiError("VALIDATION_ERROR", "Request validation failed", Instant.now(), fields);
    }

    record ApiError(String code, String message, Instant timestamp, Map<String, String> fields) {
    }
}

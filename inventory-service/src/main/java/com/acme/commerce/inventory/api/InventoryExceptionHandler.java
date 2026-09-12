package com.acme.commerce.inventory.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError badRequest(IllegalArgumentException ex) {
        return new ApiError("BAD_REQUEST", ex.getMessage(), Instant.now());
    }

    record ApiError(String code, String message, Instant timestamp) {
    }
}

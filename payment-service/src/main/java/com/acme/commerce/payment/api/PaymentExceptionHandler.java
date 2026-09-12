package com.acme.commerce.payment.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError notFound(IllegalArgumentException ex) {
        return new ApiError("NOT_FOUND", ex.getMessage(), Instant.now());
    }

    record ApiError(String code, String message, Instant timestamp) {
    }
}

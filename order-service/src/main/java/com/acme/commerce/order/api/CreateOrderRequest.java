package com.acme.commerce.order.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID customerId,
        @NotBlank @Size(max = 160) String paymentMethodToken,
        @NotEmpty List<@Valid Line> items
) {
    public record Line(
            @NotBlank @Size(max = 80) String sku,
            @NotBlank @Size(max = 180) String productName,
            @Min(1) int quantity,
            @NotNull @DecimalMin(value = "0.01") BigDecimal unitPrice
    ) {
    }
}

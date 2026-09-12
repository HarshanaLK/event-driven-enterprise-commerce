package com.acme.commerce.events;

import java.math.BigDecimal;

public record OrderLinePayload(
        String sku,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {
}

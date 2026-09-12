package com.acme.commerce.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(
        UUID eventId,
        UUID correlationId,
        Instant occurredAt,
        UUID orderId,
        UUID customerId,
        BigDecimal totalAmount,
        String paymentMethodToken,
        List<OrderLinePayload> items
) {
}

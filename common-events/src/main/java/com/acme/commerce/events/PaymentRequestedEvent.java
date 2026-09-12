package com.acme.commerce.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(
        UUID eventId,
        UUID correlationId,
        Instant occurredAt,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        String paymentMethodToken
) {
}

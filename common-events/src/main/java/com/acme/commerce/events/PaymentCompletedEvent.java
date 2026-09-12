package com.acme.commerce.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID correlationId,
        Instant occurredAt,
        UUID orderId,
        UUID paymentId,
        String providerReference
) {
}

package com.acme.commerce.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryReleaseRequestedEvent(
        UUID eventId,
        UUID correlationId,
        Instant occurredAt,
        UUID orderId,
        String reason
) {
}

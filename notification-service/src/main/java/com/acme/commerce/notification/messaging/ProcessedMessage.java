package com.acme.commerce.notification.messaging;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "processed_message")
public class ProcessedMessage {

    @Id
    @Column(name = "event_id", length = 36, nullable = false)
    private String eventId;

    @Column(name = "consumer_name", length = 120, nullable = false)
    private String consumerName;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected ProcessedMessage() {
    }

    public ProcessedMessage(String eventId, String consumerName) {
        this.eventId = eventId;
        this.consumerName = consumerName;
        this.processedAt = Instant.now();
    }
}

package com.acme.commerce.inventory.outbox;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "aggregate_id", length = 64, nullable = false)
    private String aggregateId;

    @Column(name = "event_type", length = 120, nullable = false)
    private String eventType;

    @Column(name = "topic_name", length = 180, nullable = false)
    private String topic;

    @Column(name = "event_key", length = 120, nullable = false)
    private String eventKey;

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "LONGTEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    protected OutboxEvent() {
    }

    public OutboxEvent(String aggregateId, String eventType, String topic, String eventKey, String payload) {
        this.id = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.nextAttemptAt = this.createdAt;
    }

    public void markPublished() {
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(String message) {
        this.attempts++;
        long delaySeconds = Math.min(60, 1L << Math.min(attempts, 5));
        this.nextAttemptAt = Instant.now().plus(delaySeconds, ChronoUnit.SECONDS);
        String safeMessage = message == null ? "Unknown publish error" : message;
        this.lastError = safeMessage.substring(0, Math.min(safeMessage.length(), 1000));
    }

    public String getId() { return id; }
    public String getTopic() { return topic; }
    public String getEventKey() { return eventKey; }
    public String getPayload() { return payload; }
}

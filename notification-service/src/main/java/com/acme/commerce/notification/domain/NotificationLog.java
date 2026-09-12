package com.acme.commerce.notification.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @Column(length = 32, nullable = false)
    private String type;

    @Column(length = 32, nullable = false)
    private String channel;

    @Column(length = 500, nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected NotificationLog() {
    }

    public NotificationLog(String orderId, String customerId, String type, String channel, String message) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.customerId = customerId;
        this.type = type;
        this.channel = channel;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getType() { return type; }
    public String getChannel() { return channel; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}

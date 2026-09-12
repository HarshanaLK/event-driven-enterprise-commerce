package com.acme.commerce.payment.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "order_id", length = 36, nullable = false, unique = true)
    private String orderId;

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 24, nullable = false)
    private PaymentStatus status;

    @Column(name = "provider_reference", length = 120)
    private String providerReference;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Payment() {
    }

    public Payment(String orderId, String customerId, BigDecimal amount) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void capture(String providerReference) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be captured");
        }
        this.status = PaymentStatus.CAPTURED;
        this.providerReference = providerReference;
        this.updatedAt = Instant.now();
    }

    public void fail(String reason) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can fail");
        }
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public String getProviderReference() { return providerReference; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
}

package com.acme.commerce.order.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer_order")
public class CommerceOrder {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @Column(name = "idempotency_key", length = 120, nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "payment_method_token", length = 160, nullable = false)
    private String paymentMethodToken;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderItem> items = new ArrayList<>();

    protected CommerceOrder() {
    }

    public CommerceOrder(UUID customerId, String idempotencyKey, String paymentMethodToken) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId.toString();
        this.idempotencyKey = idempotencyKey;
        this.paymentMethodToken = paymentMethodToken;
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void addItem(String sku, String productName, int quantity, BigDecimal unitPrice) {
        OrderItem item = new OrderItem(this, sku, productName, quantity, unitPrice);
        items.add(item);
        totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        touch();
    }

    public void markPaymentPending() {
        requireStatus(OrderStatus.PENDING);
        this.status = OrderStatus.PAYMENT_PENDING;
        touch();
    }

    public void confirm() {
        requireStatus(OrderStatus.PAYMENT_PENDING);
        this.status = OrderStatus.CONFIRMED;
        touch();
    }

    public void cancel(String reason) {
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Confirmed orders cannot be cancelled by the checkout saga");
        }
        if (status == OrderStatus.CANCELLED) {
            return;
        }
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        touch();
    }

    private void requireStatus(OrderStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("Expected order status " + expected + " but was " + status);
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getPaymentMethodToken() { return paymentMethodToken; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getCancellationReason() { return cancellationReason; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return List.copyOf(items); }
}

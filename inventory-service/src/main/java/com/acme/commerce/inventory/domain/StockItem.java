package com.acme.commerce.inventory.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "stock_item")
public class StockItem {

    @Id
    @Column(length = 80, nullable = false)
    private String sku;

    @Column(name = "product_name", length = 180, nullable = false)
    private String productName;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected StockItem() {
    }

    public StockItem(String sku, String productName, int availableQuantity) {
        if (availableQuantity < 0) throw new IllegalArgumentException("Initial stock cannot be negative");
        this.sku = sku;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
        this.updatedAt = Instant.now();
    }

    public boolean canReserve(int quantity) {
        return quantity > 0 && availableQuantity >= quantity;
    }

    public void reserve(int quantity) {
        if (!canReserve(quantity)) {
            throw new IllegalStateException("Insufficient stock for " + sku);
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
        updatedAt = Instant.now();
    }

    public void release(int quantity) {
        if (quantity <= 0 || reservedQuantity < quantity) {
            throw new IllegalStateException("Invalid release quantity for " + sku);
        }
        reservedQuantity -= quantity;
        availableQuantity += quantity;
        updatedAt = Instant.now();
    }

    public void commit(int quantity) {
        if (quantity <= 0 || reservedQuantity < quantity) {
            throw new IllegalStateException("Invalid commit quantity for " + sku);
        }
        reservedQuantity -= quantity;
        updatedAt = Instant.now();
    }

    public void adjustAvailable(int delta) {
        if (availableQuantity + delta < 0) {
            throw new IllegalArgumentException("Adjustment would make available stock negative");
        }
        availableQuantity += delta;
        updatedAt = Instant.now();
    }

    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public int getAvailableQuantity() { return availableQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public Instant getUpdatedAt() { return updatedAt; }
}

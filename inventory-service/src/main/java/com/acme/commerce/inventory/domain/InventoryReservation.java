package com.acme.commerce.inventory.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservation")
public class InventoryReservation {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "order_id", length = 36, nullable = false, unique = true)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 24, nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> items = new ArrayList<>();

    protected InventoryReservation() {
    }

    public InventoryReservation(String orderId) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.status = ReservationStatus.RESERVED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void addItem(String sku, int quantity) {
        items.add(new ReservationItem(this, sku, quantity));
    }

    public void release() {
        if (status == ReservationStatus.RELEASED) return;
        if (status == ReservationStatus.COMMITTED) {
            throw new IllegalStateException("Committed reservation cannot be released");
        }
        status = ReservationStatus.RELEASED;
        updatedAt = Instant.now();
    }

    public void commit() {
        if (status == ReservationStatus.COMMITTED) return;
        if (status == ReservationStatus.RELEASED) {
            throw new IllegalStateException("Released reservation cannot be committed");
        }
        status = ReservationStatus.COMMITTED;
        updatedAt = Instant.now();
    }

    public String getOrderId() { return orderId; }
    public ReservationStatus getStatus() { return status; }
    public List<ReservationItem> getItems() { return List.copyOf(items); }
}

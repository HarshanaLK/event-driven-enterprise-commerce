package com.acme.commerce.inventory.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_reservation_item")
public class ReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private InventoryReservation reservation;

    @Column(length = 80, nullable = false)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    protected ReservationItem() {
    }

    ReservationItem(InventoryReservation reservation, String sku, int quantity) {
        this.reservation = reservation;
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
}

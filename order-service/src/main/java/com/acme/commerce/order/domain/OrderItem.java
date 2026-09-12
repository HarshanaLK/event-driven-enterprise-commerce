package com.acme.commerce.order.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CommerceOrder order;

    @Column(length = 80, nullable = false)
    private String sku;

    @Column(name = "product_name", length = 180, nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    protected OrderItem() {
    }

    OrderItem(CommerceOrder order, String sku, String productName, int quantity, BigDecimal unitPrice) {
        this.order = order;
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}

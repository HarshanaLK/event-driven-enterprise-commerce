package com.acme.commerce.order.api;

import com.acme.commerce.order.domain.CommerceOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String id,
        String customerId,
        String status,
        BigDecimal totalAmount,
        String cancellationReason,
        Instant createdAt,
        List<Line> items
) {
    public static OrderResponse from(CommerceOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCancellationReason(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new Line(
                                item.getSku(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice()
                        ))
                        .toList()
        );
    }

    public record Line(String sku, String productName, int quantity, BigDecimal unitPrice) {
    }
}

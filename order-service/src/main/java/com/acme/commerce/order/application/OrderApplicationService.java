package com.acme.commerce.order.application;

import com.acme.commerce.events.EventTopics;
import com.acme.commerce.events.OrderLinePayload;
import com.acme.commerce.events.OrderPlacedEvent;
import com.acme.commerce.order.api.CreateOrderRequest;
import com.acme.commerce.order.api.OrderResponse;
import com.acme.commerce.order.domain.CommerceOrder;
import com.acme.commerce.order.domain.OrderRepository;
import com.acme.commerce.order.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OutboxService outbox;

    public OrderApplicationService(OrderRepository orderRepository, OutboxService outbox) {
        this.orderRepository = orderRepository;
        this.outbox = outbox;
    }

    @Transactional
    public OrderResponse create(String idempotencyKey, CreateOrderRequest request) {
        return orderRepository.findByIdempotencyKey(idempotencyKey)
                .map(OrderResponse::from)
                .orElseGet(() -> createNew(idempotencyKey, request));
    }

    private OrderResponse createNew(String idempotencyKey, CreateOrderRequest request) {
        CommerceOrder order = new CommerceOrder(
                request.customerId(),
                idempotencyKey,
                request.paymentMethodToken()
        );

        request.items().forEach(line ->
                order.addItem(line.sku(), line.productName(), line.quantity(), line.unitPrice())
        );

        orderRepository.save(order);

        UUID orderId = UUID.fromString(order.getId());
        OrderPlacedEvent event = new OrderPlacedEvent(
                UUID.randomUUID(),
                orderId,
                Instant.now(),
                orderId,
                UUID.fromString(order.getCustomerId()),
                order.getTotalAmount(),
                order.getPaymentMethodToken(),
                order.getItems().stream()
                        .map(item -> new OrderLinePayload(
                                item.getSku(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice()
                        ))
                        .toList()
        );

        outbox.enqueue(order.getId(), EventTopics.ORDER_PLACED, order.getId(), event);
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(String orderId) {
        CommerceOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return OrderResponse.from(order);
    }
}

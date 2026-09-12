package com.acme.commerce.order.api;

import com.acme.commerce.order.application.OrderApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderApplicationService service;

    public OrderController(OrderApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse create(
            @RequestHeader("Idempotency-Key") @Size(min = 8, max = 120) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return service.create(idempotencyKey, request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable String orderId) {
        return service.get(orderId);
    }
}

package com.acme.commerce.payment.api;

import com.acme.commerce.payment.domain.Payment;
import com.acme.commerce.payment.domain.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository payments;

    public PaymentController(PaymentRepository payments) {
        this.payments = payments;
    }

    @GetMapping
    public List<PaymentResponse> list() {
        return payments.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse byOrder(@PathVariable String orderId) {
        Payment payment = payments.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for order " + orderId));

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getProviderReference(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }

    public record PaymentResponse(
            String id,
            String orderId,
            BigDecimal amount,
            String status,
            String providerReference,
            String failureReason,
            Instant createdAt
    ) {
    }
}

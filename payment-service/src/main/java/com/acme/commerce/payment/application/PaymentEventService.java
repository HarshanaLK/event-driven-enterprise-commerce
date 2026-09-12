package com.acme.commerce.payment.application;

import com.acme.commerce.events.*;
import com.acme.commerce.payment.domain.Payment;
import com.acme.commerce.payment.domain.PaymentRepository;
import com.acme.commerce.payment.gateway.PaymentDecision;
import com.acme.commerce.payment.gateway.PaymentGateway;
import com.acme.commerce.payment.messaging.ProcessedMessage;
import com.acme.commerce.payment.messaging.ProcessedMessageRepository;
import com.acme.commerce.payment.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentEventService {

    private static final String CONSUMER = "payment-service-v1";

    private final PaymentRepository payments;
    private final PaymentGateway gateway;
    private final ProcessedMessageRepository processed;
    private final OutboxService outbox;

    public PaymentEventService(
            PaymentRepository payments,
            PaymentGateway gateway,
            ProcessedMessageRepository processed,
            OutboxService outbox
    ) {
        this.payments = payments;
        this.gateway = gateway;
        this.processed = processed;
        this.outbox = outbox;
    }

    @Transactional
    public void requestPayment(PaymentRequestedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        Optional<Payment> existing = payments.findByOrderId(event.orderId().toString());
        if (existing.isPresent()) {
            markProcessed(event.eventId());
            return;
        }

        Payment payment = new Payment(
                event.orderId().toString(),
                event.customerId().toString(),
                event.amount()
        );
        payments.save(payment);

        PaymentDecision decision = gateway.charge(
                event.orderId(),
                event.amount(),
                event.paymentMethodToken()
        );

        if (decision.approved()) {
            payment.capture(decision.providerReference());

            PaymentCompletedEvent completed = new PaymentCompletedEvent(
                    UUID.randomUUID(),
                    event.correlationId(),
                    Instant.now(),
                    event.orderId(),
                    UUID.fromString(payment.getId()),
                    decision.providerReference()
            );

            outbox.enqueue(payment.getId(), EventTopics.PAYMENT_COMPLETED,
                    event.orderId().toString(), completed);
        } else {
            payment.fail(decision.failureReason());

            PaymentFailedEvent failed = new PaymentFailedEvent(
                    UUID.randomUUID(),
                    event.correlationId(),
                    Instant.now(),
                    event.orderId(),
                    UUID.fromString(payment.getId()),
                    decision.failureReason()
            );

            outbox.enqueue(payment.getId(), EventTopics.PAYMENT_FAILED,
                    event.orderId().toString(), failed);
        }

        markProcessed(event.eventId());
    }

    private boolean alreadyProcessed(UUID eventId) {
        return processed.existsById(eventId.toString());
    }

    private void markProcessed(UUID eventId) {
        processed.save(new ProcessedMessage(eventId.toString(), CONSUMER));
    }
}

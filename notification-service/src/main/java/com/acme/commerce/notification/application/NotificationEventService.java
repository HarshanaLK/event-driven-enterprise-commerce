package com.acme.commerce.notification.application;

import com.acme.commerce.events.OrderCancelledEvent;
import com.acme.commerce.events.OrderConfirmedEvent;
import com.acme.commerce.notification.domain.NotificationLog;
import com.acme.commerce.notification.domain.NotificationLogRepository;
import com.acme.commerce.notification.messaging.ProcessedMessage;
import com.acme.commerce.notification.messaging.ProcessedMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationEventService {

    private static final String CONSUMER = "notification-service-v1";

    private final NotificationLogRepository notifications;
    private final ProcessedMessageRepository processed;

    public NotificationEventService(
            NotificationLogRepository notifications,
            ProcessedMessageRepository processed
    ) {
        this.notifications = notifications;
        this.processed = processed;
    }

    @Transactional
    public void orderConfirmed(OrderConfirmedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        notifications.save(new NotificationLog(
                event.orderId().toString(),
                event.customerId().toString(),
                "ORDER_CONFIRMED",
                "EMAIL",
                "Your order " + event.orderId() + " has been confirmed."
        ));

        markProcessed(event.eventId());
    }

    @Transactional
    public void orderCancelled(OrderCancelledEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        notifications.save(new NotificationLog(
                event.orderId().toString(),
                event.customerId().toString(),
                "ORDER_CANCELLED",
                "EMAIL",
                "Your order " + event.orderId() + " was cancelled: " + event.reason()
        ));

        markProcessed(event.eventId());
    }

    private boolean alreadyProcessed(UUID eventId) {
        return processed.existsById(eventId.toString());
    }

    private void markProcessed(UUID eventId) {
        processed.save(new ProcessedMessage(eventId.toString(), CONSUMER));
    }
}

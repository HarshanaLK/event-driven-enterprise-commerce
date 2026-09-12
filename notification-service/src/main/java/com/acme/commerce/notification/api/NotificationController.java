package com.acme.commerce.notification.api;

import com.acme.commerce.notification.domain.NotificationLogRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationLogRepository notifications;

    public NotificationController(NotificationLogRepository notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public List<NotificationResponse> list() {
        return notifications.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/order/{orderId}")
    public List<NotificationResponse> byOrder(@PathVariable String orderId) {
        return notifications.findByOrderIdOrderByCreatedAtAsc(orderId).stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(com.acme.commerce.notification.domain.NotificationLog n) {
        return new NotificationResponse(
                n.getId(),
                n.getOrderId(),
                n.getType(),
                n.getChannel(),
                n.getMessage(),
                n.getCreatedAt()
        );
    }

    public record NotificationResponse(
            String id,
            String orderId,
            String type,
            String channel,
            String message,
            Instant createdAt
    ) {
    }
}

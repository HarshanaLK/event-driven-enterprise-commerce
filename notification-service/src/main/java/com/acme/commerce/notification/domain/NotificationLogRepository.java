package com.acme.commerce.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
    List<NotificationLog> findByOrderIdOrderByCreatedAtAsc(String orderId);
    List<NotificationLog> findAllByOrderByCreatedAtDesc();
}

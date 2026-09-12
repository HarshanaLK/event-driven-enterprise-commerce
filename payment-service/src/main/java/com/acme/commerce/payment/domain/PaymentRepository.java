package com.acme.commerce.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findAllByOrderByCreatedAtDesc();
}

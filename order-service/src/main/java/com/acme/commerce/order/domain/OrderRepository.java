package com.acme.commerce.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CommerceOrder, String> {
    Optional<CommerceOrder> findByIdempotencyKey(String idempotencyKey);
    List<CommerceOrder> findAllByOrderByCreatedAtDesc();
}

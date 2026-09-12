package com.acme.commerce.payment.gateway;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGateway {
    PaymentDecision charge(UUID orderId, BigDecimal amount, String paymentMethodToken);
}

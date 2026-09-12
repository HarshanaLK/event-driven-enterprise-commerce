package com.acme.commerce.payment.gateway;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

/**
 * Local development gateway.
 *
 * Tokens beginning with "fail_" are declined intentionally so the
 * compensation path can be tested without a third-party payment account.
 *
 * Never accept raw card numbers in this service.
 */
@Component
public class DeterministicPaymentGateway implements PaymentGateway {

    @Override
    public PaymentDecision charge(UUID orderId, BigDecimal amount, String paymentMethodToken) {
        if (paymentMethodToken == null || paymentMethodToken.isBlank()) {
            return PaymentDecision.declined("Missing payment method token");
        }

        if (paymentMethodToken.toLowerCase(Locale.ROOT).startsWith("fail_")) {
            return PaymentDecision.declined("Payment provider declined the transaction");
        }

        String providerReference = "demo_" + orderId.toString().replace("-", "").substring(0, 18);
        return PaymentDecision.approved(providerReference);
    }
}

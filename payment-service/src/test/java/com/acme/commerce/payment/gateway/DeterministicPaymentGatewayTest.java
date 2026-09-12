package com.acme.commerce.payment.gateway;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class DeterministicPaymentGatewayTest {

    private final DeterministicPaymentGateway gateway = new DeterministicPaymentGateway();

    @Test
    void normalTokenIsApproved() {
        PaymentDecision decision = gateway.charge(
                UUID.randomUUID(),
                new BigDecimal("150.00"),
                "tok_demo"
        );

        assertThat(decision.approved()).isTrue();
        assertThat(decision.providerReference()).startsWith("demo_");
    }

    @Test
    void failTokenExercisesCompensationFlow() {
        PaymentDecision decision = gateway.charge(
                UUID.randomUUID(),
                new BigDecimal("150.00"),
                "fail_test"
        );

        assertThat(decision.approved()).isFalse();
        assertThat(decision.failureReason()).contains("declined");
    }
}

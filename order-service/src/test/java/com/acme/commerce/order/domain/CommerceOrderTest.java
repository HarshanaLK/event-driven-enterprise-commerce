package com.acme.commerce.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CommerceOrderTest {

    @Test
    void calculatesTotalAndMovesThroughCheckoutStates() {
        CommerceOrder order = new CommerceOrder(UUID.randomUUID(), "idem-12345678", "tok_demo");
        order.addItem("SKU-A", "Keyboard", 2, new BigDecimal("25.50"));
        order.addItem("SKU-B", "Mouse", 1, new BigDecimal("10.00"));

        assertThat(order.getTotalAmount()).isEqualByComparingTo("61.00");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

        order.markPaymentPending();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);

        order.confirm();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void confirmedOrderCannotBeCancelledByCheckoutSaga() {
        CommerceOrder order = new CommerceOrder(UUID.randomUUID(), "idem-87654321", "tok_demo");
        order.addItem("SKU-A", "Keyboard", 1, new BigDecimal("25.50"));
        order.markPaymentPending();
        order.confirm();

        assertThatThrownBy(() -> order.cancel("late failure"))
                .isInstanceOf(IllegalStateException.class);
    }
}

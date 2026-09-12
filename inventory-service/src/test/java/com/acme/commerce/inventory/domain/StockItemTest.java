package com.acme.commerce.inventory.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StockItemTest {

    @Test
    void reserveReleaseAndCommitPreserveStockInvariants() {
        StockItem stock = new StockItem("SKU-1", "Keyboard", 10);

        stock.reserve(4);
        assertThat(stock.getAvailableQuantity()).isEqualTo(6);
        assertThat(stock.getReservedQuantity()).isEqualTo(4);

        stock.release(2);
        assertThat(stock.getAvailableQuantity()).isEqualTo(8);
        assertThat(stock.getReservedQuantity()).isEqualTo(2);

        stock.commit(2);
        assertThat(stock.getAvailableQuantity()).isEqualTo(8);
        assertThat(stock.getReservedQuantity()).isZero();
    }

    @Test
    void cannotReserveMoreThanAvailable() {
        StockItem stock = new StockItem("SKU-1", "Keyboard", 3);

        assertThatThrownBy(() -> stock.reserve(4))
                .isInstanceOf(IllegalStateException.class);
    }
}

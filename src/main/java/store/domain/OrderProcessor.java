package store.domain;

import store.domain.order.Order;
import store.domain.product.stock.Inventory;

public class OrderProcessor {

    private final Inventory inventory;

    public OrderProcessor(final Inventory inventory) {
        this.inventory = inventory;
    }

    public void process(final Order order) {
        inventory.validateSellable(order.getName(), order.getQuantity());
    }
}

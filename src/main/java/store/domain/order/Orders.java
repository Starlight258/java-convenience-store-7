package store.domain.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.quantity.Quantity;

public class Orders {

    private final List<Order> items;


    public Orders(final Map<String, Quantity> mappingItems) {
        this.items = new ArrayList<>();
        for (Entry<String, Quantity> entry : mappingItems.entrySet()) {
            items.add(new Order(entry.getKey(), entry.getValue()));
        }
    }

    public void addWithMerge(String productName, Quantity quantity) {
        var existingOrder = findByProductName(productName);
        if (existingOrder != null) {
            items.remove(existingOrder);
            items.add(new Order(productName, existingOrder.quantity.add(quantity)));
        } else {
            items.add(new Order(productName, quantity));
        }
    }

    private Order findByProductName(String productName) {
        return items.stream()
                .filter(order -> order.productName.equals(productName))
                .findFirst()
                .orElse(null);
    }

    public List<Order> getItems() {
        return items;
    }

    public static class Order {

        private final String productName;
        private final Quantity quantity;

        public Order(final String productName, final Quantity quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }

        public String getProductName() {
            return productName;
        }

        public Quantity getQuantity() {
            return quantity;
        }
    }
}

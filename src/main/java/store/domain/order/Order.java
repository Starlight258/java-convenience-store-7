package store.domain.order;

import java.util.List;
import java.util.Map;
import store.domain.quantity.Quantity;
import store.util.OrderTextParser;
import store.util.StoreSplitter;

public class Order {

    private final Map<String, Quantity> items;

    public Order(final String input) {
        List<String> splitText = StoreSplitter.split(input);
        this.items = OrderTextParser.parseOrders(splitText);
    }

    public void putWithDefault(String key, Quantity value) {
        items.put(key, items.getOrDefault(key, Quantity.zero()).add(value));
    }

    public Map<String, Quantity> getItems() {
        return items;
    }
}

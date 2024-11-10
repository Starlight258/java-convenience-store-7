package store.domain.player;

import java.util.Collections;
import java.util.Map;
import store.domain.quantity.Quantity;

public class Orders {

    private final Map<String, Quantity> productsToBuy;

    public Orders(final Map<String, Quantity> productsToBuy) {
        this.productsToBuy = productsToBuy;
    }

    public void put(final String productName, final Quantity quantity) {
        productsToBuy.put(productName, productsToBuy.getOrDefault(productName, Quantity.zero()).add(quantity));
    }

    public Map<String, Quantity> getProductsToBuy() {
        return Collections.unmodifiableMap(productsToBuy);
    }
}
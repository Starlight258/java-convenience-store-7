package store.domain.player;

import java.util.Collections;
import java.util.Map;
import store.domain.quantity.Quantity;

public class PurchaseOrderForms {

    private final Map<String, Quantity> productsToBuy;

    public PurchaseOrderForms(final Map<String, Quantity> productsToBuy) {
        this.productsToBuy = productsToBuy;
    }

    public void put(final String productName, final Quantity quantity) {
        productsToBuy.put(productName, quantity);
    }

    public Map<String, Quantity> getProductsToBuy() {
        return Collections.unmodifiableMap(productsToBuy);
    }
}

package store.domain.player;

import java.util.Collections;
import java.util.Map;

public class PurchaseOrderForms {

    private final Map<String, Integer> productsToBuy;

    public PurchaseOrderForms(final Map<String, Integer> productsToBuy) {
        this.productsToBuy = productsToBuy;
    }

    public void put(final String productName, final int quantity) {
        productsToBuy.put(productName, quantity);
    }

    public Map<String, Integer> getProductsToBuy() {
        return Collections.unmodifiableMap(productsToBuy);
    }
}

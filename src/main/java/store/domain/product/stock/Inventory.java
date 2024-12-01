package store.domain.product.stock;

import java.util.LinkedHashMap;
import java.util.Map;
import store.domain.product.Product;
import store.domain.promotion.Promotion;

public class Inventory {

    private final Map<Product, Stock> inventory;

    public Inventory() {
        this.inventory = new LinkedHashMap<>();
    }

    public Map<Product, Stock> getInventory() {
        return inventory;
    }

    public void add(final Product product, final int quantity, final Promotion promotion) {
        if (inventory.containsKey(product)) {
            Stock stock = inventory.get(product);
            stock.add(quantity, promotion);
            return;
        }
        inventory.put(product, new Stock(quantity, promotion));
    }
}

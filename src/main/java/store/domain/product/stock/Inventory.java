package store.domain.product.stock;

import java.util.LinkedHashMap;
import java.util.Map;
import store.domain.product.Product;

public class Inventory {

    private final Map<String, ProductStock> inventory;

    public Inventory() {
        this.inventory = new LinkedHashMap<>();
    }

    public Map<String, ProductStock> getInventory() {
        return inventory;
    }

    public void addProductStock(final Product product, final int quantity) {
        String productName = product.getName();
        ProductStock productStock = inventory.getOrDefault(productName, new ProductStock(product));
        productStock.addQuantity(product, quantity, productStock);
        inventory.put(productName, productStock);
    }

}

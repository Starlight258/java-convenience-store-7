package store.domain.product.stock;

import java.util.LinkedHashMap;
import java.util.Map;
import store.domain.product.Product;
import store.exception.CustomIllegalArgumentException;
import store.exception.ErrorMessage;

public class Inventory {

    private final Map<String, ProductStock> inventory;

    public Inventory() {
        this.inventory = new LinkedHashMap<>();
    }

    public Map<String, ProductStock> getInventory() {
        return inventory;
    }

    public void validateProduct(final String productName) {
        if (!inventory.containsKey(productName)) {
            throw new CustomIllegalArgumentException(ErrorMessage.INVALID_PRODUCT);
        }
    }

    public ProductStock getProductStock(final String productName) {
        validateProduct(productName);
        return inventory.get(productName);
    }

    public void addProductStock(final Product product, final int quantity) {
        String productName = product.getName();
        ProductStock productStock = inventory.getOrDefault(productName, new ProductStock(product));
        productStock.addQuantity(product, quantity, productStock);
        inventory.put(productName, productStock);
    }

}

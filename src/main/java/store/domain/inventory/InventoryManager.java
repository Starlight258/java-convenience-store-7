package store.domain.inventory;

import store.domain.Store;
import store.domain.quantity.Quantity;

public class InventoryManager {

    private final Inventories inventories;

    public InventoryManager(final Inventories inventories) {
        this.inventories = inventories;
    }

    public Inventories findProductsByName(final String productName) {
        return inventories.findProductsByName(productName);
    }

    public Inventory findNoPromotionInventory(String productName) {
        Inventories productInventories = findProductsByName(productName);
        return productInventories.findNoPromotionInventory();
    }

    public void buyProductWithoutPromotion(String productName, Quantity quantity, Store store) {
        Inventories productInventories = findProductsByName(productName);
        productInventories.buyProductWithoutPromotion(quantity, store);
    }
}

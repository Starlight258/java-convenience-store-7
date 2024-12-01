package store.domain.product.stock;

import static store.exception.ErrorMessage.INVALID_PRODUCT;
import static store.exception.ErrorMessage.OUT_OF_STOCK;

import java.util.LinkedHashMap;
import java.util.Map;
import store.domain.product.Product;
import store.domain.promotion.Promotion;
import store.exception.CustomIllegalArgumentException;

public class Inventory {

    private final Map<Product, Stock> inventory;

    public Inventory() {
        this.inventory = new LinkedHashMap<>();
    }

    public Map<Product, Stock> getInventory() {
        return inventory;
    }

    public void validateSellable(final String name, final int quantity) {
        // 수량이 재고수량 이하인지 확인
        Product product = findByName(name);
        Stock stock = inventory.get(product);
        if (stock.getTotalQuantity() < quantity) {
            throw new CustomIllegalArgumentException(OUT_OF_STOCK);
        }
    }

    public Product findByName(final String name) {
        return inventory.keySet().stream()
                .filter(stock -> stock.hasName(name))
                .findFirst()
                .orElseThrow(() -> new CustomIllegalArgumentException(INVALID_PRODUCT));
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

package store.domain.inventory;

import static store.exception.ExceptionMessages.NOT_EXIST_PRODUCT;
import static store.exception.ExceptionMessages.NO_PROMOTION_PRODUCT;
import static store.exception.ExceptionMessages.OUT_OF_STOCK;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import store.domain.Store;
import store.domain.quantity.Quantity;
import store.exception.ExceptionMessages;

public class Inventories {

    private final Set<Inventory> inventories;

    public Inventories(final List<Inventory> inventories) {
        validateInventories(inventories);
        this.inventories = new LinkedHashSet<>();
        Map<String, List<Inventory>> groups = groupByProductName(inventories);
        for (List<Inventory> group : groups.values()) {
            addInventory(group);
        }
    }

    private void addInventory(final List<Inventory> group) {
        group.stream()
                .filter(inventory -> inventory.getPromotionName() != null)
                .forEach(this.inventories::add);
        group.stream()
                .filter(inv -> inv.getPromotionName() == null)
                .forEach(this.inventories::add);
    }

    public void getPurchasedItems(final Map<String, Quantity> purchasedItems) {
        for (Entry<String, Quantity> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            Quantity quantity = entry.getValue();
            Inventories sameProductInventories = findProducts(productName);
            Quantity totalStock = sameProductInventories.getTotalStocks();
            notExistProductName(sameProductInventories);
            totalOutOfStock(quantity, totalStock);
        }
    }

    public void buyProductWithoutPromotion(final Quantity quantity, final Store store) {
        Quantity totalQuantity = quantity;
        for (Inventory inventory : inventories) {
            totalQuantity = processQuantity(store, totalQuantity, inventory);
            if (totalQuantity == null) {
                return;
            }
        }
    }

    public Inventory findNoPromotionInventory() {
        for (Inventory inventory : inventories) {
            if (inventory.hasNoPromotion()) {
                return inventory;
            }
        }
        throw new IllegalStateException(NO_PROMOTION_PRODUCT.getMessageWithPrefix());
    }

    private Quantity processQuantity(final Store store, Quantity totalQuantity, final Inventory inventory) {
        Quantity subtractQuantity = inventory.subtractMaximum(totalQuantity);
        totalQuantity = totalQuantity.subtract(subtractQuantity);
        store.noteNoPromotionProduct(inventory.getProduct(), subtractQuantity);
        if (totalQuantity.hasZeroValue()) {
            return null;
        }
        return totalQuantity;
    }

    private Map<String, List<Inventory>> groupByProductName(final List<Inventory> inventories) {
        return inventories.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getProduct().getName(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private void totalOutOfStock(final Quantity quantity, final Quantity totalStock) {
        if (totalStock.isLessThan(quantity)) {
            throw new IllegalArgumentException(OUT_OF_STOCK.getMessageWithPrefix());
        }
    }

    private void notExistProductName(final Inventories sameProductInventories) {
        if (sameProductInventories.getInventories().size() == 0) {
            throw new IllegalArgumentException(NOT_EXIST_PRODUCT.getMessageWithPrefix());
        }
    }

    private void validateInventories(final List<Inventory> inventories) {
        if (inventories == null) {
            throw new IllegalArgumentException(ExceptionMessages.NOT_NULL_ARGUMENT.getMessageWithPrefix());
        }
    }

    public Inventories findProducts(final String productName) {
        Inventories sameProducts = new Inventories(Collections.emptyList());
        for (Inventory inventory : inventories) {
            if (inventory.isSameProductName(productName)) {
                sameProducts.add(inventory);
            }
        }
        return sameProducts;
    }

    public void add(final Inventory inventory) {
        inventories.add(inventory);
    }

    public Quantity getTotalStocks() {
        Quantity total = Quantity.zero();
        for (Inventory inventory : inventories) {
            total = total.add(inventory.getQuantity());
        }
        return total;
    }

    public Set<Inventory> getInventories() {
        return Collections.unmodifiableSet(inventories);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Inventories that = (Inventories) o;
        return Objects.equals(inventories, that.inventories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventories);
    }
}

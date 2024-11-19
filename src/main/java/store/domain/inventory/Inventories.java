package store.domain.inventory;

import static store.exception.ExceptionMessages.NOT_EXIST_PRODUCT;
import static store.exception.ExceptionMessages.NO_PROMOTION_PRODUCT;
import static store.exception.ExceptionMessages.OUT_OF_STOCK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import store.domain.Store;
import store.domain.quantity.Quantity;
import store.exception.ExceptionMessages;

public class Inventories {

    private final List<Inventory> inventories;

    public Inventories(final List<Inventory> inventories) {
        validateInventories(inventories);
        this.inventories = sort(inventories);
    }

    private List<Inventory> sort(final List<Inventory> inventories) {
        return inventories.stream()
                .sorted(this::compareInventory)
                .collect(Collectors.toList());
    }

    private int compareInventory(final Inventory source, final Inventory target) {
        if (!isSameProduct(source, target)) {
            return 0;
        }
        return comparePromotion(source, target);
    }

    private int comparePromotion(final Inventory source, final Inventory target) {
        if (source.hasPromotion() && target.hasNoPromotion()) {
            return -1;
        }
        if (source.hasNoPromotion() && target.hasPromotion()) {
            return 1;
        }
        return 0;
    }

    private boolean isSameProduct(final Inventory source, final Inventory target) {
        return source.hasSameProductName(target);
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
        Inventories sameProducts = new Inventories(new ArrayList<>());
        for (Inventory inventory : inventories) {
            if (inventory.isSameProductName(productName)) {
                sameProducts.add(inventory);
            }
        }
        return sameProducts;
    }

    public void add(final Inventory inventory) {
        addOrderly(inventory);
    }

    private void addOrderly(final Inventory inventory) {
        int index = findIndex(inventory);
        inventories.add(index, inventory);
    }

    private int findIndex(final Inventory newInventory) {
        String productName = newInventory.getProductName();
        if (newInventory.hasPromotion()) {
            return findFirstIndexOfProduct(productName);
        }
        return findLastIndexOfProduct(productName);
    }

    private int findLastIndexOfProduct(final String productName) {
        int index = inventories.size();
        for (int i = 0; i < inventories.size(); i++) {
            Inventory currentInventory = inventories.get(i);
            if (currentInventory.getProductName().equals(productName)) {
                index = i + 1;
            }
        }
        return index;
    }

    private int findFirstIndexOfProduct(final String productName) {
        return IntStream.range(0, inventories.size())
                .filter(index -> inventories.get(index).getProductName().equals(productName))
                .findFirst()
                .orElse(inventories.size());
    }

    public Quantity getTotalStocks() {
        Quantity total = Quantity.zero();
        for (Inventory inventory : inventories) {
            total = total.add(inventory.getQuantity());
        }
        return total;
    }

    public List<Inventory> getInventories() {
        return Collections.unmodifiableList(inventories);
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

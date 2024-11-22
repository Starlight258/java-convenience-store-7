package store.domain.inventory;

import static store.exception.ErrorMessage.OUT_OF_STOCK;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import store.domain.Store;
import store.domain.order.Orders;
import store.domain.order.Orders.Order;
import store.domain.quantity.Quantity;
import store.exception.CustomIllegalArgumentException;
import store.exception.CustomIllegalStateException;
import store.exception.ErrorMessage;

public class Inventories {

    private static final String PROMOTION_NOT_FOUND = "프로모션이 없는 상품을 찾을 수 없습니다.";
    private static final String PRODUCT_NOT_FOUND = "존재하지 않는 상품입니다. 다시 입력해 주세요.";

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

    public void checkStock(final Orders purchasedItems) {
        for (Order order : purchasedItems.getItems()) {
            String productName = order.getProductName();
            Quantity quantity = order.getQuantity();
            Inventories sameProductInventories = findProductsByName(productName);
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
        throw new CustomIllegalStateException(PROMOTION_NOT_FOUND);
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
            throw new CustomIllegalArgumentException(OUT_OF_STOCK.getMessage());
        }
    }

    private void notExistProductName(final Inventories sameProductInventories) {
        if (sameProductInventories.getInventories().size() == 0) {
            throw new CustomIllegalArgumentException(PRODUCT_NOT_FOUND);
        }
    }

    private void validateInventories(final List<Inventory> inventories) {
        if (inventories == null) {
            throw new CustomIllegalArgumentException(ErrorMessage.NULL.getMessage());
        }
    }

    public Inventories findProductsByName(final String productName) {
        return new Inventories(inventories.stream()
                .filter(inventory -> inventory.isSameProductName(productName))
                .toList());
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

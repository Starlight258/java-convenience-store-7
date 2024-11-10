package store.domain.inventory;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import store.domain.Store;
import store.domain.player.PurchaseOrderForms;
import store.domain.quantity.Quantity;

public class Inventories {

    private final TreeSet<Inventory> inventories;

    public Inventories(final List<Inventory> inventories) {
        validateInventories(inventories);
        this.inventories = new TreeSet<>(inventories);
    }

    public void getPurchasedItems(final PurchaseOrderForms purchasedItems) {
        for (Entry<String, Quantity> entry : purchasedItems.getProductsToBuy().entrySet()) {
            String productName = entry.getKey();
            Quantity quantity = entry.getValue();
            Inventories sameProductInventories = findProducts(productName);
            Quantity totalStock = sameProductInventories.getTotalStocks(); // 프로모션X + 프로모션O
            notExistProductName(sameProductInventories);
            totalOutOfStock(quantity, totalStock);
        }
    }

    public void buyProductWithoutPromotion(final Quantity quantity, final Store store) {
        Quantity totalQuantity = quantity;
        for (Inventory inventory : inventories) {
            Quantity subtractQuantity = inventory.subtractMaximum(totalQuantity);
            totalQuantity = totalQuantity.subtract(subtractQuantity);
            store.noteNoPromotionProduct(inventory.getProduct(), subtractQuantity);
            if (totalQuantity.hasZeroValue()) {
                return;
            }
        }
    }

    private void totalOutOfStock(final Quantity quantity, final Quantity totalStock) {
        if (totalStock.isLessThan(quantity)) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private void notExistProductName(final Inventories sameProductInventories) {
        if (sameProductInventories.getInventories().size() == 0) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
    }

    private void validateInventories(final List<Inventory> inventories) {
        if (inventories == null) {
            throw new IllegalArgumentException("[ERROR] 인벤토리 리스트는 null일 수 없습니다.");
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

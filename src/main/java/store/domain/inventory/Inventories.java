package store.domain.inventory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import store.domain.membership.Membership;
import store.domain.receipt.Receipt;

public class Inventories {

    private final TreeSet<Inventory> inventories;

    public Inventories(final List<Inventory> inventories) {
        validateInventories(inventories);
        this.inventories = new TreeSet<>(inventories);
    }

    public void getPurchasedItems(final Map<String, Integer> purchasedItems) {
        for (Entry<String, Integer> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            Inventories sameProductInventories = findProducts(productName);
            int totalStock = sameProductInventories.getTotalStocks(); // 프로모션X + 프로모션O
            notExistProductName(sameProductInventories);
            totalOutOfStock(quantity, totalStock);
        }
    }

    public void buyProductWithoutPromotion(final int quantity, final Membership membership, final Receipt receipt) {
        int totalQuantity = quantity;
        for (Inventory inventory : inventories) {
            int subtractQuantity = inventory.subtractMaximum(totalQuantity);
            totalQuantity -= subtractQuantity;
            membership.addNoPromotionProduct(inventory.getProduct(), subtractQuantity);
            receipt.purchaseProducts(inventory.getProduct(), subtractQuantity);
            if (totalQuantity == 0) {
                return;
            }
        }
    }

    private void totalOutOfStock(final int quantity, final int totalStock) {
        if (totalStock < quantity) {
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

    public int getTotalStocks() {
        int total = 0;
        for (Inventory inventory : inventories) {
            total += inventory.getQuantity();
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

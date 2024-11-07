package store;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class Inventories {

    private final TreeSet<Inventory> inventories;

    public Inventories(final List<Inventory> inventories) {
        validateInventories(inventories);
        this.inventories = new TreeSet<>(inventories);
    }

    private void validateInventories(final List<Inventory> inventories) {
        if (inventories == null) {
            throw new IllegalArgumentException("[ERROR] 인벤토리 리스트는 null일 수 없습니다.");
        }
    }

    public Inventories findProducts(final Product product) {
        Inventories sameProducts = new Inventories(Collections.emptyList());
        for (Inventory inventory : inventories) {
            if (inventory.isSameProduct(product)) {
                sameProducts.add(inventory);
            }
        }
        return sameProducts;
    }

    public void add(final Inventory inventory) {
        inventories.add(inventory);
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

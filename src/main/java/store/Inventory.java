package store;

import java.util.Objects;

public class Inventory implements Comparable<Inventory> {

    public static final String ERROR = "[ERROR]";

    private final Product product;
    private int quantity;
    private final Promotion promotion;

    public Inventory(final Product product, final int quantity, final Promotion promotion) {
        validate(product, quantity);
        this.product = product;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public boolean isSameProduct(Product product) {
        return this.product.equals(product);
    }

    public boolean buy(final int purchaseQuantity) {
        if (quantity >= purchaseQuantity) {
            this.quantity -= purchaseQuantity;
            return true;
        }
        return false;
    }

    private void validate(final Product product, final int quantity) {
        if (product == null) {
            throw new IllegalArgumentException(ERROR + " 상품은 null일 수 없습니다.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException(ERROR + " 개수는 음수일 수 없습니다.");
        }
    }

    @Override
    public int compareTo(final Inventory other) {
        if (other.promotion == null) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Inventory inventory = (Inventory) o;
        return quantity == inventory.quantity && Objects.equals(product, inventory.product)
                && Objects.equals(promotion, inventory.promotion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, promotion);
    }
}

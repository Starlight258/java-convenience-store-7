package store;

import java.util.Objects;

public class Inventory implements Comparable<Inventory> {

    public static final String ERROR = "[ERROR]";

    private final Product product;
    private int quantity;
    private final String promotionName;

    public Inventory(final Product product, final int quantity, final String promotionName) {
        validate(product, quantity);
        this.product = product;
        this.quantity = quantity;
        this.promotionName = promotionName;
    }

    public boolean isSameProductName(String productName) {
        return this.product.isSameProductName(productName);
    }

    public void buy(final int purchaseQuantity) {
        if (quantity >= purchaseQuantity) {
            this.quantity -= purchaseQuantity;
            return;
        }
        throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
    }

    private void validate(final Product product, final int quantity) {
        if (product == null) {
            throw new IllegalArgumentException(ERROR + " 상품은 null일 수 없습니다.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException(ERROR + " 개수는 음수일 수 없습니다.");
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPromotionName() {
        return promotionName;
    }

    @Override
    public int compareTo(final Inventory o) {
        if (o.promotionName == null) {
            return -1;
        }
        return 1;
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
                && Objects.equals(promotionName, inventory.promotionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, promotionName);
    }
}

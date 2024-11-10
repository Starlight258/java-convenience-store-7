package store.domain.inventory;

import java.util.Objects;
import store.domain.price.Price;
import store.domain.quantity.Quantity;
import store.exception.ExceptionMessage;
import store.exception.ExceptionMessages;

public class Inventory implements Comparable<Inventory> {

    private static final ExceptionMessage OUT_OF_STOCK = new ExceptionMessage("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");

    private final Product product;
    private Quantity quantity;
    private final String promotionName;

    public Inventory(final Product product, final int quantity, final String promotionName) {
        validate(product);
        this.product = product;
        this.quantity = new Quantity(quantity);
        this.promotionName = promotionName;
    }

    public boolean isSameProductName(String productName) {
        return this.product.isSameProductName(productName);
    }

    public void subtract(final Quantity purchaseQuantity) {
        if (quantity.isMoreThanEqual(purchaseQuantity)) {
            quantity = quantity.subtract(purchaseQuantity);
            return;
        }
        throw new IllegalArgumentException(OUT_OF_STOCK.getMessage());
    }

    public Quantity subtractMaximum(final Quantity purchaseQuantity) {
        if (quantity.isMoreThanEqual(purchaseQuantity)) {
            quantity = quantity.subtract(purchaseQuantity);
            return purchaseQuantity;
        }
        Quantity totalQuantity = this.quantity;
        this.quantity = Quantity.zero();
        return totalQuantity;
    }

    public String getProductName() {
        return product.getName();
    }

    public Product getProduct() {
        return product;
    }

    private void validate(final Product product) {
        if (product == null) {
            throw new IllegalArgumentException(ExceptionMessages.NOT_NULL_ARGUMENT.getErrorMessage());
        }
    }

    public Quantity getQuantity() {
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

    public Price getProductPrice() {
        return product.getPrice();
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

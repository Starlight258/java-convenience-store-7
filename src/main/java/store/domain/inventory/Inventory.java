package store.domain.inventory;

import static store.exception.ExceptionMessages.OUT_OF_STOCK;

import java.util.Objects;
import store.domain.quantity.Quantity;
import store.util.InputValidator;

public class Inventory {

    private static final String NULL = "null";

    private final Product product;
    private Quantity quantity;
    private final String promotionName;

    public Inventory(final Product product, final int quantity, final String promotionName) {
        validate(product, promotionName);
        this.product = product;
        this.quantity = new Quantity(quantity);
        this.promotionName = promotionName;
    }

    public boolean isSameProductName(String productName) {
        return this.product.isSameProductName(productName);
    }

    public Quantity subtract(final Quantity purchaseQuantity) {
        if (quantity.isMoreThanEqual(purchaseQuantity)) {
            quantity = quantity.subtract(purchaseQuantity);
            return quantity;
        }
        throw new IllegalArgumentException(OUT_OF_STOCK.getMessageWithPrefix());
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

    public boolean hasSameProductName(Inventory inventory) {
        return this.getProductName().equals(inventory.getProductName());
    }

    public boolean hasNoPromotion() {
        return NULL.equals(promotionName);
    }

    public boolean hasPromotion() {
        return !NULL.equals(promotionName);
    }

    public String getProductName() {
        return product.getName();
    }

    public Product getProduct() {
        return product;
    }

    private void validate(final Product product, final String promotionName) {
        InputValidator.validateNotNull(product);
        InputValidator.validateNotNullOrBlank(promotionName);
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public String getPromotionName() {
        return promotionName;
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
        return IsEqual(inventory);
    }

    private boolean IsEqual(final Inventory inventory) {
        return quantity == inventory.quantity && Objects.equals(product, inventory.product)
                && Objects.equals(promotionName, inventory.promotionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, promotionName);
    }
}

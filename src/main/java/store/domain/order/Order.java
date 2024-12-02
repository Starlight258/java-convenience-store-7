package store.domain.order;

import static store.exception.ErrorMessage.INVALID_ORDER_FORMAT;

import java.util.Objects;
import store.exception.CustomIllegalArgumentException;

public class Order {

    private final String productName;
    private final int quantity;

    public Order(final String productName, final int quantity) {
        validate(quantity);
        this.productName = productName;
        this.quantity = quantity;
    }

    private void validate(final int quantity) {
        if (quantity <= 0) {
            throw new CustomIllegalArgumentException(INVALID_ORDER_FORMAT);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order order)) {
            return false;
        }
        return Objects.equals(getName(), order.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public String getName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}

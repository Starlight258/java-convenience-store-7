package store.domain.quantity;

import java.util.Objects;
import store.exception.ExceptionMessage;

public class Quantity {

    public static final ExceptionMessage EXCEPTION_CANNOT_MINUS = new ExceptionMessage("수량은 음수일 수 없습니다.");

    private int quantity;

    public Quantity(final int quantity) {
        validate(quantity);
        this.quantity = quantity;
    }

    public static Quantity zero() {
        return new Quantity(0);
    }

    public static Quantity one() {
        return new Quantity(1);
    }

    public Quantity add(final Quantity value) {
        return new Quantity(this.quantity + value.getQuantity());
    }

    public Quantity subtract(final Quantity value) {
        if (this.quantity < value.quantity) {
            throw new IllegalArgumentException(EXCEPTION_CANNOT_MINUS.getMessage());
        }
        return new Quantity(this.quantity - value.quantity);
    }

    public Quantity multiply(final Quantity value) {
        return new Quantity(this.quantity * value.quantity);
    }

    public Quantity divide(final Quantity value) {
        return new Quantity(this.quantity / value.quantity);
    }

    public boolean isMoreThanEqual(final Quantity value) {
        return quantity >= value.getQuantity();
    }

    public boolean isMoreThan(final Quantity value) {
        return quantity > value.getQuantity();
    }

    public boolean isLessThan(final Quantity value) {
        return quantity < value.quantity;
    }

    public boolean hasZeroValue() {
        return this.quantity == 0;
    }

    private void validate(final int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(EXCEPTION_CANNOT_MINUS.getMessage());
        }
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Quantity other = (Quantity) o;
        return quantity == other.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity);
    }
}

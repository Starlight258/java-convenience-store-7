package store.domain.price;

import java.math.BigDecimal;
import java.util.Objects;
import store.exception.ExceptionMessage;
import store.exception.ExceptionMessages;

public class Price {

    private static final ExceptionMessage INVALID_PRICE_CONTAINS_NOT_NUMBER = new ExceptionMessage(
            "가격은 숫자로만 이루어져야 합니다.");
    private static final ExceptionMessage INVALID_PRICE_NOT_MINUS = new ExceptionMessage("가격은 음수일 수 없습니다.");

    private final BigDecimal price;

    public Price(final BigDecimal price) {
        validate(price);
        this.price = price;
    }

    public static Price zero() {
        return new Price(BigDecimal.ZERO);
    }

    public Price add(final Price value) {
        return new Price(price.add(value.getPrice()));
    }

    public Price subtract(final Price value) {
        return new Price(price.subtract(value.getPrice()));
    }

    public Price divide(final BigDecimal value) {
        return new Price(price.divide(value));
    }

    public Price multiply(final BigDecimal value) {
        return new Price(price.multiply(value));
    }

    public boolean isEqualOrMoreThan(final BigDecimal value) {
        return price.compareTo(value) > 0;
    }

    private void validate(final BigDecimal price) {
        validateIsNull(price);
        validateNumber(price);
    }

    private void validateNumber(final BigDecimal price) {
        if (price.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException(INVALID_PRICE_CONTAINS_NOT_NUMBER.getMessage());
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(INVALID_PRICE_NOT_MINUS.getMessage());
        }
    }

    private void validateIsNull(final BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException(ExceptionMessages.NOT_NULL_ARGUMENT.getErrorMessage());
        }
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Price other = (Price) o;
        return Objects.equals(price, other.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }
}

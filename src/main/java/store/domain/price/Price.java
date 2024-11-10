package store.domain.price;

import java.math.BigDecimal;
import java.util.Objects;

public class Price {

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
        if (price == null) {
            throw new IllegalArgumentException("[ERROR] 가격은 null일 수 없습니다.");
        }
        if (price.stripTrailingZeros().scale() > 0) {  // 소수점 확인
            throw new IllegalArgumentException("[ERROR] 가격은 숫자로만 이루어져야 합니다.");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("[ERROR] 가격은 음수일 수 없습니다.");
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
        Price price1 = (Price) o;
        return Objects.equals(price, price1.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }
}
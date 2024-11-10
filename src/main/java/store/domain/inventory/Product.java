package store.domain.inventory;

import java.math.BigDecimal;
import java.util.Objects;
import store.domain.price.Price;
import store.exception.ExceptionMessage;

public class Product {

    private static final ExceptionMessage INVALID_PRODUCT_NAME = new ExceptionMessage("상품명은 비어있거나 null일 수 없습니다.");

    private final String name;
    private final Price price;

    public Product(final String name, final BigDecimal price) {
        validateName(name);
        this.name = name;
        this.price = new Price(price);
    }

    public boolean isSameProductName(String name) {
        return this.name.equals(name);
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(INVALID_PRODUCT_NAME.getMessage());
        }
    }

    public Price getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return Objects.equals(name, product.name) && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}

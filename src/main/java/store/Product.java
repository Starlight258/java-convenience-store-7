package store;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private final String name;
    private final BigDecimal price;

    public Product(final String name, final BigDecimal price) {
        validateName(name);
        validatePrice(price);
        this.name = name;
        this.price = price;
    }

    public boolean isSameProductName(String name){
        return this.name.equals(name);
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 상품명은 비어있거나 null일 수 없습니다.");
        }
    }

    private void validatePrice(final BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("[ERROR] 가격은 null일 수 없습니다.");
        }
    }

    public BigDecimal getPrice() {
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

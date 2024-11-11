package store.domain.inventory;

import java.math.BigDecimal;
import java.util.Objects;
import store.domain.price.Price;
import store.util.InputValidator;

public class Product {

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
        InputValidator.validateNotNullOrBlank(name);
    }

    public Price getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Product product = (Product) other;
        return Objects.equals(name, product.name) && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}

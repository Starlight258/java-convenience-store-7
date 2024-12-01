package store.domain.product;

public class Product {

    private final String name;
    private final int price;

    public Product(final String name, final int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean hasName(final String name) {
        return this.name.equals(name);
    }
}

package store.domain.product;

import store.domain.promotion.Promotion;

public class Product {

    private final String name;
    private final int price;
    private Promotion promotion;

    public Product(final String name, final int price, final Promotion promotion) {
        this.name = name;
        this.price = price;
        this.promotion = promotion;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean hasPromotion() {
        return this.promotion != null;
    }

    public void setPromotion(final Promotion promotion) {
        this.promotion = promotion;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}

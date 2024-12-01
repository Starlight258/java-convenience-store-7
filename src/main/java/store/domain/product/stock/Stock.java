package store.domain.product.stock;

import store.domain.promotion.Promotion;

public class Stock {

    private static final int NOT_EXIST = -1;

    private final Promotion promotion;
    private int promotionQuantity;
    private int regularQuantity;

    public Stock(final Promotion promotion) {
        this.promotion = promotion;
        this.promotionQuantity = NOT_EXIST;
        this.regularQuantity = 0;
    }

    public Stock(final int quantity, final Promotion promotion) {
        this(promotion);
        if (promotion == null) {
            this.regularQuantity = quantity;
            return;
        }
        promotionQuantity = quantity;
    }

    public void add(final int quantity, final Promotion promotion) {
        if (promotion == null) {
            this.regularQuantity += quantity;
            return;
        }
        if (promotionQuantity == NOT_EXIST) {
            promotionQuantity = 0;
        }
        promotionQuantity += quantity;
    }

    public int getPromotionQuantity() {
        return promotionQuantity;
    }

    public int getRegularQuantity() {
        return regularQuantity;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}


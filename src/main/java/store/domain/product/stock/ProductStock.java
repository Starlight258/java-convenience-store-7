package store.domain.product.stock;

import static store.domain.product.stock.StockStatus.EMPTY;
import static store.domain.product.stock.StockStatus.NOT_EXIST;

import store.domain.product.Product;
import store.domain.promotion.Promotion;

public class ProductStock {

    private final Product product;
    private int promotionQuantity;
    private int regularQuantity;

    public ProductStock(final Product product) {
        this.product = product;
        this.promotionQuantity = initializePromotionQuantity();
        this.regularQuantity = initializeRegularQuantity();
    }

    private int initializeRegularQuantity() {
        return EMPTY.getValue();
    }

    private int initializePromotionQuantity() {
        return NOT_EXIST.getValue();
    }

    public void add(final int quantity, final Promotion promotion) {
        if (promotion == null) {
            addRegularQuantity(quantity);
            return;
        }
        addPromotionQuantity(quantity);
    }

    public int getTotalQuantity() {
        if (isPromotionInitialValue()) {
            return regularQuantity;
        }
        return promotionQuantity + regularQuantity;
    }

    private boolean isPromotionInitialValue() {
        return promotionQuantity == NOT_EXIST.getValue();
    }

    public int getPromotionQuantity() {
        return promotionQuantity;
    }

    public int getRegularQuantity() {
        return regularQuantity;
    }

    public void addPromotionQuantity(final int quantity) {
        if (isPromotionInitialValue()) {
            promotionQuantity = 0;
        }
        promotionQuantity += quantity;
    }

    public void addRegularQuantity(final int quantity) {
        this.regularQuantity += quantity;
    }

    public void addQuantity(final Product product, final int quantity, final ProductStock productStock) {
        if (product.hasPromotion()) {
            productStock.addPromotionQuantity(quantity);
            productStock.setPromotion(product.getPromotion());
            return;
        }
        productStock.addRegularQuantity(quantity);
    }

    private void setPromotion(final Promotion promotion) {
        product.setPromotion(promotion);
    }

    public Product getProduct() {
        return product;
    }
}


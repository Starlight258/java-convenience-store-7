package store.domain.product.stock;

import static store.domain.product.stock.StockStatus.EMPTY;
import static store.domain.product.stock.StockStatus.NOT_EXIST;

import store.domain.product.Product;
import store.domain.promotion.Promotion;
import store.exception.CustomIllegalArgumentException;
import store.exception.ErrorMessage;

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

    public Promotion getPromotion() {
        return product.getPromotion();
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

    public void subtractRegularQuantity(int purchaseQuantity) {
        if (regularQuantity >= purchaseQuantity) {
            regularQuantity -= purchaseQuantity;
            return;
        }
        throw new CustomIllegalArgumentException(ErrorMessage.OUT_OF_STOCK);
    }

    private void setPromotion(final Promotion promotion) {
        product.setPromotion(promotion);
    }

    public Product getProduct() {
        return product;
    }

    public void checkTotalStock(final int purchaseQuantity) {
        int totalQuantity = purchaseQuantity + regularQuantity;
        if (totalQuantity < purchaseQuantity) {
            throw new CustomIllegalArgumentException(ErrorMessage.OUT_OF_STOCK);
        }
    }

    public boolean cannotPurchaseWithinPromotion(final int purchaseQuantity) {
        return promotionQuantity < purchaseQuantity;
    }
}


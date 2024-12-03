package store.service;

import store.domain.product.stock.ProductStock;
import store.domain.promotion.PromotionProcessor;

public class ProcessingContext {

    private final ProductStock productStock;
    private final PromotionProcessor promotionProcessor;

    public ProcessingContext(final ProductStock productStock, final PromotionProcessor promotionProcessor) {
        this.productStock = productStock;
        this.promotionProcessor = promotionProcessor;
    }

    public ProductStock getProductStock() {
        return productStock;
    }

    public PromotionProcessor getPromotionProcessor() {
        return promotionProcessor;
    }
}

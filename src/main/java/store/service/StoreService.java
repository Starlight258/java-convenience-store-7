package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import store.domain.order.Order;
import store.domain.product.stock.Inventory;
import store.domain.product.stock.ProductStock;
import store.domain.promotion.PromotionProcessor;
import store.domain.promotion.PromotionResult;

public class StoreService {

    public PromotionResult processOrder(final Inventory inventory, final Order order) {
        ProcessingContext processingContext = createProcessingContext(inventory, order.getName());
        LocalDate now = DateTimes.now().toLocalDate();
        return processingContext.getPromotionProcessor().processOrder(order.getQuantity(), now);
    }

    public void processRegularPayment(final Inventory inventory, final Order order,
                                      final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(inventory, order.getName());
        processingContext.getPromotionProcessor().processWithRegularPayment(promotionResult);
    }

    public ProcessingContext createProcessingContext(final Inventory inventory, final String productName) {
        ProductStock productStock = inventory.getProductStock(productName);
        PromotionProcessor promotionProcessor = new PromotionProcessor(productStock);
        return new ProcessingContext(productStock, promotionProcessor);
    }

    public void processOnlyPromotionPayment(final Inventory inventory, final Order order,
                                            final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(inventory, order.getName());
        processingContext.getPromotionProcessor().processOnlyPromotionPayment(promotionResult);
    }

    public void processBenefitOption(final Inventory inventory, final Order order,
                                     final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(inventory, order.getName());
        processingContext.getPromotionProcessor().processBenefitOption(promotionResult);
    }

    public void processNoBenefitOption(final Inventory inventory, final Order order,
                                       final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(inventory, order.getName());
        processingContext.getPromotionProcessor().processNoBenefitOption(promotionResult);
    }
}

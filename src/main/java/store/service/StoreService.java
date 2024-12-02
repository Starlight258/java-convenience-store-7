package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import store.domain.order.Order;
import store.domain.product.stock.Inventory;
import store.domain.product.stock.ProductStock;
import store.domain.promotion.PromotionProcessor;
import store.domain.promotion.PromotionResult;

public class StoreService {

    public void processOrder(final Inventory inventory, final Order order) {
        ProductStock productStock = inventory.getProductStock(order.getName());
        PromotionProcessor promotionProcessor = new PromotionProcessor(productStock);
        LocalDate now = DateTimes.now().toLocalDate();
        PromotionResult promotionResult = promotionProcessor.process(order.getQuantity(), now);
    }

}

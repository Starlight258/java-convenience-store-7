package store.service;

import java.util.LinkedHashMap;
import store.domain.PurchaseContext;
import store.domain.Store;
import store.domain.membership.Membership;
import store.domain.order.Orders;
import store.domain.order.Orders.Order;
import store.domain.price.Price;
import store.domain.promotion.PromotionProcessor;
import store.domain.receipt.Receipt;
import store.response.Response;
import store.response.ResponseHandler;
import store.view.InteractionView;

public class StoreService {

    private final InteractionView interactionView;
    private PromotionProcessor promotionProcessor;

    public StoreService(final InteractionView interactionView) {
        this.interactionView = interactionView;
    }

    public void initialize(final PromotionProcessor promotionProcessor) {
        this.promotionProcessor = promotionProcessor;
    }

    public Store initializeStore() {
        return new Store(new Receipt(new LinkedHashMap<>(), new LinkedHashMap<>()),
                new Membership(new LinkedHashMap<>()));
    }

    public void processPurchase(Orders orders, Store store) {
        PurchaseContext context = new PurchaseContext();
        promotionProcessor.getInventories().checkStock(orders);
        for (Order order : orders.getItems()) {
            Response response = promotionProcessor.pay(order, store, context);
            ResponseHandler handler = new ResponseHandler(orders, store, order, interactionView);
            handler.handle(response);
        }
    }

    public Price checkMembership(final boolean useMembership, final Membership membership) {
        if (useMembership) {
            return membership.calculateDiscount();
        }
        return Price.zero();
    }
}

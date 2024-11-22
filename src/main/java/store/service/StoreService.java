package store.service;

import java.util.LinkedHashMap;
import store.domain.PurchaseContext;
import store.domain.Store;
import store.domain.inventory.InventoryManager;
import store.domain.membership.Membership;
import store.domain.order.Orders;
import store.domain.order.Orders.Order;
import store.domain.payment.PaymentProcessor;
import store.domain.price.Price;
import store.domain.receipt.Receipt;
import store.response.Response;
import store.response.ResponseHandler;
import store.view.InteractionView;

public class StoreService {

    private final InteractionView interactionView;
    private InventoryManager inventoryManager;
    private PaymentProcessor paymentProcessor;

    public StoreService(final InteractionView interactionView) {
        this.interactionView = interactionView;
    }

    public void initializeProcessor(final InventoryManager inventoryManager, final PaymentProcessor paymentProcessor) {
        this.inventoryManager = inventoryManager;
        this.paymentProcessor = paymentProcessor;
    }

    public Store initializeStore() {
        return new Store(new Receipt(new LinkedHashMap<>(), new LinkedHashMap<>()),
                new Membership(new LinkedHashMap<>()));
    }

    public void processOrder(final Orders orders, final Order order, final Store store, final PurchaseContext context) {
        Response response = paymentProcessor.findPurchaseOptions(order, store);
        ResponseHandler handler = new ResponseHandler(orders, store, order, interactionView, inventoryManager);
        handler.handle(response);
    }

    public Price checkMembership(final boolean useMembership, final Membership membership) {
        if (useMembership) {
            return membership.calculateDiscount();
        }
        return Price.zero();
    }
}

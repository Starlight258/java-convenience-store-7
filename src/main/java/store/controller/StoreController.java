package store.controller;

import java.util.List;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.membership.Membership;
import store.domain.order.Orders;
import store.domain.price.Price;
import store.domain.promotion.PromotionProcessor;
import store.domain.promotion.Promotions;
import store.exception.ExceptionHandler;
import store.service.StoreService;
import store.util.OrderTextParser;
import store.util.StoreInitializer;
import store.util.StoreSplitter;
import store.view.StoreView;

public class StoreController {

    private final StoreView storeView;
    private final ExceptionHandler exceptionHandler;
    private final StoreService storeService;
    private final StoreInitializer storeInitializer;

    public StoreController(final StoreView storeView, final ExceptionHandler exceptionHandler,
                           final StoreService storeService,
                           final StoreInitializer storeInitializer) {
        this.storeView = storeView;
        this.exceptionHandler = exceptionHandler;
        this.storeService = storeService;
        this.storeInitializer = storeInitializer;
    }

    public void process() {
        Inventories inventories = exceptionHandler.tryWithThrow(storeInitializer::loadInventories);
        Promotions promotions = exceptionHandler.tryWithThrow(storeInitializer::loadPromotions);
        storeService.initializeProcessor(new PromotionProcessor(inventories, promotions));
        processTransactions(inventories);
    }

    private void processTransactions(final Inventories inventories) {
        while (true) {
            if (exceptionHandler.tryWithoutThrow(() -> processTransaction(inventories))) {
                storeView.showBlankLine();
                continue;
            }
            return;
        }
    }

    private boolean processTransaction(final Inventories inventories) {
        showWelcomeMessage(inventories);
        Orders orders = createOrder(inventories);
        processOrder(orders);
        return continueTransaction();
    }

    private void showWelcomeMessage(final Inventories inventories) {
        storeView.showStartMessage();
        storeView.showInventories(inventories);
    }

    private Orders createOrder(final Inventories inventories) {
        storeView.showCommentOfPurchase();
        return exceptionHandler.retryOn(() -> {
            String input = storeView.readLine();
            List<String> splitText = StoreSplitter.split(input);
            Orders orders = new Orders(OrderTextParser.parseOrders(splitText));
            inventories.checkStock(orders);
            return orders;
        });
    }

    private void processOrder(final Orders orders) {
        Store store = storeService.initializeStore();
        Price membershipPrice = processPurchaseAndMembership(orders, store);
        storeView.showResults(store.getReceipt(), membershipPrice);
    }

    private Price processPurchaseAndMembership(Orders orders, Store store) {
        storeService.processPurchase(orders, store);
        return checkMembership(store.getMembership());
    }

    private Price checkMembership(final Membership membership) {
        storeView.showCommentOfMemberShip();
        return storeService.checkMembership(storeView.isYes(), membership);
    }

    private boolean continueTransaction() {
        storeView.askAdditionalPurchase();
        return storeView.isYes();
    }
}

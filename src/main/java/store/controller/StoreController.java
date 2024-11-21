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
        Inventories inventories = exceptionHandler.actionOfFileReadWithReturn(storeInitializer::loadInventories);
        Promotions promotions = exceptionHandler.actionOfFileReadWithReturn(storeInitializer::loadPromotions);
        showWelcomeMessage(inventories);
        storeService.initialize(new PromotionProcessor(inventories, promotions));
        processTransactions();
    }

    private void processTransactions() {
        while (true) {
            if (exceptionHandler.tryWithReturn(this::processTransaction)) {
                return;
            }
            storeView.showBlankLine();
        }
    }

    private boolean processTransaction() {
        Orders orders = createOrder();
        processStore(orders);
        return !continueTransaction();
    }

    private void showWelcomeMessage(final Inventories inventories) {
        storeView.showStartMessage();
        storeView.showInventories(inventories);
    }

    private Orders createOrder() {
        storeView.showCommentOfPurchase();
        return exceptionHandler.retryWithReturn(() -> {
            String input = storeView.readLine();
            List<String> splitText = StoreSplitter.split(input);
            return new Orders(OrderTextParser.parseOrders(splitText));
        });
    }

    private void processStore(final Orders orders) {
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
        return storeService.checkMembership(storeView.readAnswer(), membership);
    }

    private boolean continueTransaction() {
        storeView.showAdditionalPurchase();
        return storeView.readAnswer();
    }
}

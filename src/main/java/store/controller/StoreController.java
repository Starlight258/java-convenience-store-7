package store.controller;

import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.membership.Membership;
import store.domain.order.Order;
import store.domain.price.Price;
import store.domain.promotion.PromotionProcessor;
import store.exception.ExceptionHandler;
import store.service.StoreService;
import store.view.StoreView;

public class StoreController {

    private final StoreView storeView;
    private final ExceptionHandler exceptionHandler;
    private final StoreService storeService;

    public StoreController(final StoreView storeView, final ExceptionHandler exceptionHandler,
                           final StoreService storeService) {
        this.storeView = storeView;
        this.exceptionHandler = exceptionHandler;
        this.storeService = storeService;
    }

    public void process() {
        PromotionProcessor promotionProcessor = exceptionHandler.actionOfFileRead(
                storeService::initializePaymentSystem);
        processTransactions(promotionProcessor);
    }

    private void processTransactions(final PromotionProcessor promotionProcessor) {
        while (true) {
            if (exceptionHandler.tryWithReturn(() -> processTransaction(promotionProcessor))) {
                return;
            }
            storeView.showBlankLine();
        }
    }

    private boolean processTransaction(final PromotionProcessor promotionProcessor) {
        Inventories inventories = promotionProcessor.getInventories();
        showWelcomeMessage(inventories);
        Order order = getPurchasedItems(inventories);
        processStore(order, promotionProcessor);
        return !continueTransaction();
    }

    private void showWelcomeMessage(final Inventories inventories) {
        storeView.showStartMessage();
        storeView.showInventories(inventories);
    }

    private Order getPurchasedItems(final Inventories inventories) {
        storeView.showCommentOfPurchase();
        return exceptionHandler.retryWithReturn(() -> {
            String input = storeView.readLine();
            return storeService.createOrders(input, inventories);
        });
    }

    private void processStore(final Order orders, final PromotionProcessor promotionProcessor) {
        Store store = storeService.initializeStore();
        Price membershipPrice = processPurchaseAndMembership(orders, promotionProcessor, store);
        storeView.showResults(store.getReceipt(), membershipPrice);
    }

    private Price processPurchaseAndMembership(Order orders, PromotionProcessor promotionProcessor,
                                               Store store) {
        storeService.processPurchase(orders, promotionProcessor, store);
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

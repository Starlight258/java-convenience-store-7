package store.controller;

import java.util.Map;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.membership.Membership;
import store.domain.price.Price;
import store.domain.quantity.Quantity;
import store.domain.system.PaymentSystem;
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
        PaymentSystem paymentSystem = exceptionHandler.actionOfFileRead(storeService::initializePaymentSystem);
        processTransactions(paymentSystem);
    }

    private void processTransactions(final PaymentSystem paymentSystem) {
        while (true) {
            if (exceptionHandler.tryWithReturn(() -> processTransaction(paymentSystem))) {
                return;
            }
            storeView.showBlankLine();
        }
    }

    private boolean processTransaction(final PaymentSystem paymentSystem) {
        Inventories inventories = paymentSystem.getInventories();
        showWelcomeMessage(inventories);
        Map<String, Quantity> orders = getPurchasedItems(inventories);
        processStore(orders, paymentSystem);
        return !continueTransaction();
    }

    private void showWelcomeMessage(final Inventories inventories) {
        storeView.showStartMessage();
        storeView.showInventories(inventories);
    }

    private Map<String, Quantity> getPurchasedItems(final Inventories inventories) {
        storeView.showCommentOfPurchase();
        return exceptionHandler.retryWithReturn(() -> {
            String input = storeView.readLine();
            return storeService.createOrders(input, inventories);
        });
    }

    private void processStore(final Map<String, Quantity> orders, final PaymentSystem paymentSystem) {
        Store store = storeService.initializeStore();
        Price membershipPrice = processPurchaseAndMembership(orders, paymentSystem, store);
        storeView.showResults(store.getReceipt(), membershipPrice);
    }

    private Price processPurchaseAndMembership(Map<String, Quantity> orders, PaymentSystem paymentSystem, Store store) {
        storeService.processPurchase(orders, paymentSystem, store);
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

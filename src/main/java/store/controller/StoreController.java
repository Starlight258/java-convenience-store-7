package store.controller;

import java.util.List;
import java.util.Map;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.membership.Membership;
import store.domain.player.Orders;
import store.domain.price.Price;
import store.domain.receipt.Receipt;
import store.domain.system.PaymentSystem;
import store.exception.ExceptionHandler;
import store.service.StoreService;
import store.support.StoreFormatter;
import store.view.InputView;
import store.view.InteractionView;
import store.view.OutputView;

public class StoreController {

    private final InputView inputView;
    private final OutputView outputView;
    private final InteractionView interactionView;
    private final ExceptionHandler exceptionHandler;
    private final StoreService storeService;
    private final StoreFormatter storeFormatter;

    public StoreController(final InputView inputView, final OutputView outputView,
                           final InteractionView interactionView,
                           final ExceptionHandler exceptionHandler, final StoreService storeService,
                           final StoreFormatter storeFormatter) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.interactionView = interactionView;
        this.exceptionHandler = exceptionHandler;
        this.storeService = storeService;
        this.storeFormatter = storeFormatter;
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
            outputView.showBlankLine();
        }
    }

    private boolean processTransaction(final PaymentSystem paymentSystem) {
        Inventories inventories = paymentSystem.getInventories();
        showWelcomeMessage(inventories);
        Orders orders = getPurchasedItems(inventories);
        processStore(orders, paymentSystem);
        return !continueTransaction();
    }

    private void showWelcomeMessage(final Inventories inventories) {
        outputView.showStartMessage();
        showInventories(storeService.groupInventories(inventories));
    }

    public void showInventories(final Map<String, List<Inventory>> inventories) {
        inventories.values().forEach(this::showProductInventories);
    }

    private void showProductInventories(List<Inventory> inventories) {
        showPromotionInventories(inventories);
        showNormalInventories(inventories);
    }

    private void showPromotionInventories(List<Inventory> inventories) {
        inventories.stream()
                .filter(storeService::isPromotionInventory)
                .forEach(this::showInventory);
    }

    private void showNormalInventories(List<Inventory> inventories) {
        inventories.stream()
                .filter(storeService::isNormalInventory)
                .findFirst()
                .ifPresentOrElse(
                        this::showInventory,
                        () -> showInventory(storeService.createNoStockInventory(inventories.get(0)))
                );
    }

    private void showInventory(Inventory inventory) {
        outputView.showMessage(storeFormatter.makeInventoryMessage(inventory));
    }

    private Orders getPurchasedItems(final Inventories inventories) {
        outputView.showCommentOfPurchase();
        return exceptionHandler.retryWithReturn(() -> {
            String input = inputView.readLine();
            return storeService.createOrders(input, inventories);
        });
    }

    private void processStore(final Orders orders, final PaymentSystem paymentSystem) {
        Store store = storeService.initializeStore();
        Price membershipPrice = processPurchaseAndMembership(orders, paymentSystem, store);
        showResults(store.getReceipt(), membershipPrice);
    }

    private Price processPurchaseAndMembership(Orders orders, PaymentSystem paymentSystem, Store store) {
        storeService.processPurchase(orders, paymentSystem, store);
        return checkMembership(store.getMembership());
    }

    private Price checkMembership(final Membership membership) {
        outputView.showCommentOfMemberShip();
        if (!interactionView.readYOrN()) {
            return Price.zero();
        }
        return membership.calculateDiscount();
    }

    private boolean continueTransaction() {
        outputView.showAdditionalPurchase();
        return interactionView.readYOrN();
    }

    private void showResults(Receipt receipt, Price membershipPrice) {
        outputView.showPurchaseProducts(receipt);
        outputView.showBonusProducts(receipt);
        outputView.showReceipt(receipt, membershipPrice);
    }
}

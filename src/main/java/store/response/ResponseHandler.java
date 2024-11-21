package store.response;

import java.util.Map;
import java.util.function.Consumer;
import store.domain.Store;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.order.Orders;
import store.domain.order.Orders.Order;
import store.domain.quantity.Quantity;
import store.view.InteractionView;

public class ResponseHandler {

    private final Orders orders;
    private final Store store;
    private final String productName;
    private final Quantity quantity;
    private final InteractionView interactionView;

    public ResponseHandler(final Orders orders, final Store store, final Order order,
                           final InteractionView interactionView) {
        this.orders = orders;
        this.store = store;
        this.productName = order.getProductName();
        this.quantity = order.getQuantity();
        this.interactionView = interactionView;
    }

    public void handle(final Response response) {
        Map<ResponseStatus, Consumer<Response>> handlers = getHandlers();
        handlers.getOrDefault(response.status(), this::processDefault).accept(response);
    }

    private Map<ResponseStatus, Consumer<Response>> getHandlers() {
        return Map.of(
                ResponseStatus.BUY_WITH_NO_PROMOTION, this::processDefault,
                ResponseStatus.BUY_WITH_PROMOTION, this::processWithPromotion,
                ResponseStatus.OUT_OF_STOCK, this::processOutOfStock,
                ResponseStatus.CAN_GET_BONUS, this::processCanGetBonus
        );
    }

    private void processDefault(final Response response) {
    }

    private void processWithPromotion(final Response response) {
        store.noteBonusProduct(response.inventory().getProduct(), response.bonusQuantity());
    }

    private void processOutOfStock(final Response response) {
        Product product = response.inventory().getProduct();
        store.noteBonusProduct(product, response.bonusQuantity());
        askNoPromotion(response, product);
    }

    private void askNoPromotion(final Response response, final Product product) {
        Quantity noPromotionQuantity = response.noPromotionQuantity();
        Inventory noPromotionInventory = response.sameProductInventories().findNoPromotionInventory();
        if (interactionView.askForNoPromotion(productName, noPromotionQuantity.getQuantity())) {
            purchaseOnlyPromotionProduct(response, product, noPromotionInventory);
            return;
        }
        purchaseWithinPromotionStock(response, product, noPromotionQuantity);
    }

    private void purchaseOnlyPromotionProduct(final Response response, final Product product,
                                              final Inventory noPromotionInventory) {
        if (quantity.isLessThan(response.inventory().getQuantity())) {
            purchaseWithNoPromotionProduct(response, product, noPromotionInventory);
            return;
        }
        purchaseWithNoPromotionProductOfOutOfStock(response, product, noPromotionInventory);
    }

    private void purchaseWithNoPromotionProduct(final Response response, final Product product,
                                                final Inventory noPromotionInventory) {
        store.noteWithNoPromotion(product, quantity, response.noPromotionQuantity());
        Inventory inventory = response.inventory();
        Quantity quantityOfInventory = inventory.getQuantity();
        inventory.subtract(quantity);
    }

    private void purchaseWithNoPromotionProductOfOutOfStock(final Response response, final Product product,
                                                            final Inventory noPromotionInventory) {
        store.noteWithNoPromotion(product, quantity, response.noPromotionQuantity());
        Inventory inventory = response.inventory();
        Quantity quantityOfInventory = inventory.getQuantity();
        noPromotionInventory.subtract(quantity.subtract(quantityOfInventory));
        inventory.subtract(quantityOfInventory);
    }

    private void purchaseWithinPromotionStock(final Response response, final Product product,
                                              final Quantity noPromotionQuantity) {
        Quantity subtractedQuantity = quantity.subtract(noPromotionQuantity);
        store.notePurchaseProduct(product, subtractedQuantity);

        orders.addWithMerge(productName, subtractedQuantity);
        response.inventory().subtract(quantity.subtract(noPromotionQuantity));
    }

    private void processCanGetBonus(final Response response) {
        Product product = response.inventory().getProduct();
        processInitialOrder(product);
        processBonusQuantity(response, product);
    }

    private void processInitialOrder(final Product product) {
        orders.addWithMerge(productName, quantity);
        store.notePurchaseProduct(product, quantity);
    }

    private void processBonusQuantity(final Response response, final Product product) {
        Quantity bonusQuantity = response.bonusQuantity();
        Quantity canGetMoreQuantity = response.canGetMoreQuantity();
        Inventory inventory = response.inventory();
        if (interactionView.askForBonus(productName, canGetMoreQuantity)) {
            processAdditionalPurchase(product, bonusQuantity, canGetMoreQuantity, inventory);
            return;
        }
        keepOriginalPurchase(product, bonusQuantity, canGetMoreQuantity, inventory);
    }

    private void processAdditionalPurchase(final Product product, final Quantity bonusQuantity,
                                           final Quantity canGetMoreQuantity,
                                           final Inventory inventory) {
        store.noteAddingMoreQuantity(product, bonusQuantity, canGetMoreQuantity);
        orders.addWithMerge(productName, quantity.add(canGetMoreQuantity));
        inventory.subtract(quantity.add(canGetMoreQuantity));
    }

    private void keepOriginalPurchase(final Product product, final Quantity bonusQuantity,
                                      final Quantity canGetMoreQuantity,
                                      final Inventory inventory) {
        store.noteBonusProduct(product, bonusQuantity.subtract(canGetMoreQuantity));
        inventory.subtract(quantity);
    }
}

package store.response;

import java.util.Map;
import java.util.function.Consumer;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.player.Orders;
import store.domain.quantity.Quantity;
import store.view.InteractionView;

public class ResponseHandler {

    private final Orders orders;
    private final Store store;
    private final String productName;
    private final Quantity quantity;
    private final InteractionView interactionView;

    public ResponseHandler(final Orders orders, final Store store, final String productName, final Quantity quantity,
                           final InteractionView interactionView) {
        this.orders = orders;
        this.store = store;
        this.productName = productName;
        this.quantity = quantity;
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
        Inventories inventories = response.sameProductInventories();
        Inventory noPromotionInventory = inventories.findNoPromotionInventory();
        if (interactionView.askForNoPromotion(productName, noPromotionQuantity.getQuantity())) {
            purchaseWithNoPromotionProduct(response, product, noPromotionInventory);
            return;
        }
        purchaseOnlyPromotionProduct(response, product, noPromotionQuantity);
    }

    private void purchaseWithNoPromotionProduct(final Response response, final Product product,
                                                final Inventory noPromotionInventory) {
        store.noteNoPromotionProduct(product, quantity);
        Inventory inventory = response.inventory();
        Quantity quantityOfInventory = inventory.getQuantity();
        noPromotionInventory.subtract(quantity.subtract(quantityOfInventory));
        inventory.subtract(quantityOfInventory);
    }

    private void purchaseOnlyPromotionProduct(final Response response, final Product product,
                                              final Quantity noPromotionQuantity) {
        Quantity subtractedQuantity = quantity.subtract(noPromotionQuantity);
        store.notePurchaseProduct(product, subtractedQuantity);
        orders.put(productName, subtractedQuantity);
        response.inventory().subtract(quantity.subtract(noPromotionQuantity));
    }

    private void processCanGetBonus(final Response response) {
        Product product = response.inventory().getProduct();
        processInitialOrder(product);
        processBonusQuantity(response, product);
    }

    private void processInitialOrder(final Product product) {
        orders.put(productName, quantity);
        store.notePurchaseProduct(product, quantity);
    }

    private void processBonusQuantity(final Response response, final Product product) {
        Quantity bonusQuantity = response.bonusQuantity();
        Quantity canGetMoreQuantity = response.canGetMoreQuantity();
        if (interactionView.askForBonus(productName, canGetMoreQuantity)) {
            store.noteAddingMoreQuantity(product, bonusQuantity, canGetMoreQuantity);
            orders.put(productName, quantity.add(canGetMoreQuantity));
        }
        store.noteBonusProduct(product, bonusQuantity.subtract(canGetMoreQuantity));
    }
}

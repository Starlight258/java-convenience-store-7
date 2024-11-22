package store.domain.payment;

import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.InventoryManager;
import store.domain.inventory.Product;
import store.domain.order.Orders.Order;
import store.domain.promotion.Promotion;
import store.domain.promotion.PromotionManager;
import store.domain.promotion.PromotionQuantities;
import store.domain.quantity.Quantity;
import store.exception.CustomIllegalStateException;
import store.response.Response;

public class PaymentProcessor {

    private static final String PRODUCT_NOT_PURCHASE = "상품을 구매할 수 없습니다.";

    private final InventoryManager inventoryManager;
    private final PromotionManager promotionManager;

    public PaymentProcessor(final InventoryManager inventoryManager, final PromotionManager promotionManager) {
        this.inventoryManager = inventoryManager;
        this.promotionManager = promotionManager;
    }

    public Response findPurchaseOptions(Order order, Store store) {
        Inventory inventory = findPriorityInventory(order);
        Quantity quantity = order.getQuantity();
        if (inventory.hasNoPromotion()) {
            return payNormal(inventory, quantity, store);
        }
        return purchaseWithPromotions(inventory, quantity, store);
    }

    private Inventory findPriorityInventory(final Order order) {
        Inventories productInventories = inventoryManager.findProductsByName(order.getProductName());
        for (Inventory inventory : productInventories.getInventories()) {
            if (inventory.hasNoPromotion() || promotionManager.isValidPromotion(inventory.getPromotionName())) {
                return inventory;
            }
        }
        throw new CustomIllegalStateException(PRODUCT_NOT_PURCHASE);
    }

    private Response purchaseWithPromotions(Inventory inventory, Quantity quantity, Store store) {
        Promotion promotion = promotionManager.findByName(inventory.getPromotionName());
        PromotionQuantities promotionQuantities = calculatePromotionQuantities(promotion);
        if (isOutOfStock(inventory.getQuantity(), quantity)) {
            return handleWithLack(inventory, quantity, promotionQuantities, store);
        }
        if (exceedsPromotionLimit(quantity, promotionQuantities)) {
            return handleMixedPurchase(inventory, quantity, promotionQuantities);
        }
        return applyPromotionRules(inventory, quantity, promotionQuantities, store);
    }

    private Response applyPromotionRules(Inventory inventory, Quantity quantity,
                                         PromotionQuantities promotionQuantities, Store store) {
        if (isLessThanMinimumPurchase(quantity, promotionQuantities.purchaseQuantity())) {
            return payNormal(inventory, quantity, store);
        }
        if (canGetAdditionalItems(quantity, promotionQuantities)) {
            return askForBonus(inventory, quantity, promotionQuantities, store);
        }
        return purchaseWithPromotion(quantity, promotionQuantities, inventory, store);
    }

    private Response askForBonus(final Inventory inventory, final Quantity quantity,
                                 final PromotionQuantities promotionQuantities, final Store store) {
        if (inventory.getQuantity().equals(quantity)) {
            return payNormal(inventory, quantity, store);
        }
        return createBonusResponse(quantity, promotionQuantities, inventory);
    }

    private boolean exceedsPromotionLimit(final Quantity quantity, final PromotionQuantities promotionQuantities) {
        Quantity bonusQuantity = promotionQuantities.bonusQuantity();
        Quantity purchaseQuantity = promotionQuantities.purchaseQuantity();
        Quantity promotionUnit = bonusQuantity.add(purchaseQuantity);
        return quantity.isMoreThan(promotionUnit) && !quantity.remainder(promotionUnit).equals(Quantity.zero());
    }

    private Response payNormal(Inventory inventory, Quantity quantity, Store store) {
        purchaseWithoutPromotion(store, quantity, inventory);
        return Response.purchaseWithNoPromotion(inventory);
    }

    private PromotionQuantities calculatePromotionQuantities(Promotion promotion) {
        return PromotionQuantities.from(promotion);
    }

    private boolean isOutOfStock(Quantity stock, Quantity requestedQuantity) {
        return stock.isLessThan(requestedQuantity);
    }

    private Response handleWithLack(Inventory inventory, Quantity quantity,
                                    PromotionQuantities promotionQuantities,
                                    Store store) {
        if (canApplyPartialPromotion(inventory.getQuantity(), promotionQuantities)) {
            return createPartialPromotionResponseForOutOfStock(inventory, quantity, promotionQuantities);
        }
        return purchaseWithNoPromotion(inventory, quantity, store);
    }

    private boolean canApplyPartialPromotion(Quantity stock, PromotionQuantities promotionQuantities) {
        return stock.isMoreThanEqual(promotionQuantities.totalQuantity());
    }

    private Response handleMixedPurchase(Inventory inventory, Quantity quantity,
                                         PromotionQuantities promotionQuantities) {
        Quantity setSize = calculateSetSize(quantity, promotionQuantities);
        Quantity totalBonusQuantity = calculateTotalBonus(setSize, promotionQuantities);
        Quantity noPromotionQuantity = calculateNoPromotionQuantity(quantity, setSize, promotionQuantities);
        return Response.outOfStock(totalBonusQuantity, noPromotionQuantity, inventory);
    }

    private Response createPartialPromotionResponseForOutOfStock(Inventory inventory, Quantity quantity,
                                                                 PromotionQuantities promotionQuantities) {
        Quantity setSize = calculateSetSize(inventory.getQuantity(), promotionQuantities);
        Quantity totalBonusQuantity = calculateTotalBonus(setSize, promotionQuantities);
        Quantity noPromotionQuantity = calculateNoPromotionQuantity(quantity, setSize, promotionQuantities);
        return Response.outOfStock(totalBonusQuantity, noPromotionQuantity, inventory);
    }

    private Quantity calculateSetSize(Quantity stock, PromotionQuantities promotionQuantities) {
        return stock.divide(promotionQuantities.totalQuantity());
    }

    private Quantity calculateTotalBonus(Quantity setSize, PromotionQuantities promotionQuantities) {
        return setSize.multiply(promotionQuantities.bonusQuantity());
    }

    private Quantity calculateNoPromotionQuantity(Quantity quantity, Quantity setSize,
                                                  PromotionQuantities promotionQuantities) {
        return quantity.subtract(setSize.multiply(promotionQuantities.totalQuantity()));
    }

    private Response purchaseWithNoPromotion(Inventory inventory, Quantity quantity, Store store) {
        inventoryManager.buyProductWithoutPromotion(inventory.getProductName(), quantity, store);
        return Response.purchaseWithNoPromotion(inventory);
    }

    private boolean isLessThanMinimumPurchase(Quantity quantity, Quantity minimumQuantity) {
        return quantity.isLessThan(minimumQuantity);
    }

    private boolean canGetAdditionalItems(Quantity quantity, PromotionQuantities promotionQuantities) {
        int purchaseUnit = promotionQuantities.purchaseQuantity().getQuantity();
        return quantity.getQuantity() == purchaseUnit;
    }

    private Response createBonusResponse(final Quantity quantity,
                                         final PromotionQuantities promotionQuantities, Inventory inventory) {
        Quantity freeQuantity = calculateFreeQuantity(quantity, promotionQuantities);
        return Response.canGetMoreQuantity(promotionQuantities.bonusQuantity(), freeQuantity, inventory);
    }

    private Quantity calculateFreeQuantity(Quantity quantity, PromotionQuantities promotionQuantities) {
        return promotionQuantities.totalQuantity().subtract(quantity);
    }

    private Response purchaseWithPromotion(Quantity quantity,
                                           PromotionQuantities promotionQuantities,
                                           Inventory inventory, Store store) {
        Quantity setSize = calculateSetSize(quantity, promotionQuantities);
        Quantity totalBonusQuantity = calculateTotalBonus(setSize, promotionQuantities);
        completePurchase(quantity, inventory, store);
        return Response.buyWithPromotion(totalBonusQuantity, inventory);
    }

    private void completePurchase(Quantity quantity, Inventory inventory, Store store) {
        inventory.subtract(quantity);
        store.notePurchaseProduct(inventory.getProduct(), quantity);
    }

    private void purchaseWithoutPromotion(final Store store, final Quantity totalQuantity,
                                          final Inventory inventory) {
        inventory.subtract(totalQuantity);
        Product product = inventory.getProduct();
        store.noteNoPromotionProduct(product, totalQuantity);
    }
}

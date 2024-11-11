package store.domain.system;

import static store.exception.ExceptionMessages.CANNOT_BUY_PRODUCT;

import java.time.LocalDate;
import java.util.Optional;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.response.Response;

public class PaymentSystem {

    private static final String NULL = "null";

    private final Inventories inventories;
    private final Promotions promotions;

    public PaymentSystem(final Inventories inventories, final Promotions promotions) {
        this.inventories = inventories;
        this.promotions = promotions;
    }

    public Response pay(final String productName, final Quantity quantity,
                        final Store store, final LocalDate now) {
        Inventories sameProductInventories = inventories.findProducts(productName);
        return findPurchaseOptions(sameProductInventories, quantity, store, now);
    }

    private Response findPurchaseOptions(Inventories sameProductInventories, Quantity quantity,
                                         Store store, LocalDate now) {
        for (Inventory inventory : sameProductInventories.getInventories()) {
            Response response = determinePromotion(inventory, quantity, store, sameProductInventories, now);
            if (response != null) {
                return response;
            }
        }
        throw new IllegalStateException(CANNOT_BUY_PRODUCT.getMessageWithPrefix());
    }

    private Response determinePromotion(Inventory inventory, Quantity quantity,
                                        Store store, Inventories sameProductInventories, LocalDate now) {
        if (hasNoPromotion(inventory)) {
            return payNormal(inventory, quantity, store);
        }
        Optional<Promotion> optionalPromotion = findValidPromotion(inventory.getPromotionName(), now);
        return optionalPromotion.map(
                        promotion -> purchaseWithPromotion(inventory, quantity, store, sameProductInventories, promotion))
                .orElse(null);
    }

    private Response purchaseWithPromotion(Inventory inventory, Quantity quantity,
                                           Store store, Inventories sameProductInventories,
                                           Promotion promotion) {
        PromotionQuantities promotionQuantities = calculatePromotionQuantities(promotion);
        if (isOutOfStock(inventory.getQuantity(), quantity)) {
            return handleWithLack(inventory, quantity, promotionQuantities, store, sameProductInventories);
        }
        if (exceedsPromotionLimit(quantity, promotionQuantities)) {
            return handleMixedPurchase(inventory, quantity, promotionQuantities, sameProductInventories);
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

    private boolean hasNoPromotion(Inventory inventory) {
        return inventory.getPromotionName().equals(NULL);
    }

    private Response payNormal(Inventory inventory, Quantity quantity, Store store) {
        purchaseWithoutPromotion(store, quantity, inventory);
        return Response.purchaseWithNoPromotion(inventory);
    }

    private Optional<Promotion> findValidPromotion(String promotionName, LocalDate now) {
        return promotions.find(promotionName, now);
    }

    private PromotionQuantities calculatePromotionQuantities(Promotion promotion) {
        return PromotionQuantities.from(promotion);
    }

    private boolean isOutOfStock(Quantity stock, Quantity requestedQuantity) {
        return stock.isLessThan(requestedQuantity);
    }

    private Response handleWithLack(Inventory inventory, Quantity quantity,
                                    PromotionQuantities promotionQuantities,
                                    Store store, Inventories sameProductInventories) {
        if (canApplyPartialPromotion(inventory.getQuantity(), promotionQuantities)) {
            return createPartialPromotionResponseForOutOfStock(inventory, quantity, promotionQuantities,
                    sameProductInventories);
        }
        return purchaseWithNoPromotion(inventory, quantity, store, sameProductInventories);
    }

    private boolean canApplyPartialPromotion(Quantity stock, PromotionQuantities promotionQuantities) {
        return stock.isMoreThanEqual(promotionQuantities.totalQuantity());
    }

    private Response handleMixedPurchase(Inventory inventory, Quantity quantity,
                                         PromotionQuantities promotionQuantities,
                                         final Inventories sameProductInventories) {
        Quantity setSize = calculateSetSize(quantity, promotionQuantities);
        Quantity totalBonusQuantity = calculateTotalBonus(setSize, promotionQuantities);
        Quantity noPromotionQuantity = calculateNoPromotionQuantity(quantity, setSize, promotionQuantities);
        return Response.outOfStock(totalBonusQuantity, noPromotionQuantity, inventory, sameProductInventories);
    }

    private Response createPartialPromotionResponseForOutOfStock(Inventory inventory, Quantity quantity,
                                                                 PromotionQuantities promotionQuantities,
                                                                 final Inventories sameProductInventories) {
        Quantity setSize = calculateSetSize(inventory.getQuantity(), promotionQuantities);
        Quantity totalBonusQuantity = calculateTotalBonus(setSize, promotionQuantities);
        Quantity noPromotionQuantity = calculateNoPromotionQuantity(quantity, setSize, promotionQuantities);
        return Response.outOfStock(totalBonusQuantity, noPromotionQuantity, inventory, sameProductInventories);
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

    private Response purchaseWithNoPromotion(Inventory inventory, Quantity quantity,
                                             Store store, Inventories sameProductInventories) {
        sameProductInventories.buyProductWithoutPromotion(quantity, store);
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

    public Inventories getInventories() {
        return inventories;
    }
}

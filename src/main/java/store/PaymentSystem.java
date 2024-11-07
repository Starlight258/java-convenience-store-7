package store;

import java.time.LocalDate;
import java.util.Optional;

public class PaymentSystem {

    private final Inventories inventories;
    private final Promotions promotions;

    public PaymentSystem(final Inventories inventories, final Promotions promotions) {
        this.inventories = inventories;
        this.promotions = promotions;
    }

    public Response canBuy(final String productName, final int quantity, final LocalDate now) {
        Inventories sameProductInventories = inventories.findProducts(productName);
        int totalStock = sameProductInventories.getTotalStocks(); // 프로모션X + 프로모션O
        notExistProductName(sameProductInventories);
        totalOutOfStock(quantity, totalStock);
        for (Inventory inventory : sameProductInventories.getInventories()) {
            String promotionName = inventory.getPromotionName();
            Optional<Promotion> optionalPromotion = promotions.find(promotionName); // 프로모션 찾기
            Response response = buyWithNoPromotion(quantity, inventory, optionalPromotion);
            if (response != null) {
                return response;
            }
            // 할인 기간 판단
            Promotion promotion = getPromotion(now, optionalPromotion);
            if (promotion == null) {
                continue;
            }
            int purchaseQuantity = promotion.getPurchaseQuantity();
            int bonusQuantity = promotion.getBonusQuantity();
            int stock = inventory.getQuantity(); // 재고
            Response noPromotionQuantity = outOfStock(quantity, purchaseQuantity, bonusQuantity, stock);
            if (noPromotionQuantity != null) {
                return noPromotionQuantity;
            }
            // 할인 적용X
            Response noPromotion = buyWithNoPromotion(quantity, inventory, purchaseQuantity);
            if (noPromotion != null) {
                return noPromotion;
            }
            // 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 그 수량만큼 **추가 여부를 입력**받는다.
            Response canGetBonus = canGetBonus(quantity, purchaseQuantity, bonusQuantity);
            if (canGetBonus != null) {
                return canGetBonus;
            }
            // 프로모션이 자동 적용된다.
            Response BUY = autoPromotion(quantity, inventory, purchaseQuantity, bonusQuantity);
            if (BUY != null) {
                return BUY;
            }
        }
        throw new IllegalStateException("[ERROR] 상품을 구매할 수 없습니다.");
    }

    private static Response autoPromotion(final int quantity, final Inventory inventory, final int purchaseQuantity,
                                        final int bonusQuantity) {
        if (quantity > purchaseQuantity) {
            int setSize = quantity / (purchaseQuantity + bonusQuantity);
            int totalBonusQuantity = setSize * bonusQuantity; // 보너스 수량
            inventory.buy(quantity);
            return Response.buyWithPromotion(RESPONSE_STATUS.BUY, totalBonusQuantity);
        }
        return null;
    }

    private static Response canGetBonus(final int quantity, final int purchaseQuantity, final int bonusQuantity) {
        if (quantity < purchaseQuantity + bonusQuantity) {
            int freeQuantity = purchaseQuantity + bonusQuantity - quantity;
            return new Response(RESPONSE_STATUS.CAN_GET_BONUS, freeQuantity, 0);
        }
        return null;
    }

    private static Response buyWithNoPromotion(final int quantity, final Inventory inventory, final int purchaseQuantity) {
        if (quantity < purchaseQuantity) {
            inventory.buy(quantity);
            return Response.buyWithNoPromotion(RESPONSE_STATUS.BUY_WITH_NO_PROMOTION);
        }
        return null;
    }

    private static Response outOfStock(final int quantity, final int purchaseQuantity, final int bonusQuantity,
                                       final int stock) {
        if (stock < quantity) {
            int setSize = stock / (purchaseQuantity + bonusQuantity);
            int noPromotionQuantity = quantity - setSize * (purchaseQuantity + bonusQuantity);
            return Response.outOfStock(noPromotionQuantity);
        }
        return null;
    }

    private static Promotion getPromotion(final LocalDate now, final Optional<Promotion> optionalPromotion) {
        Promotion promotion = optionalPromotion.get();
        if (!promotion.isPromotionPeriod(now)) {
            return null;
        }
        return promotion;
    }

    private static Response buyWithNoPromotion(final int quantity, final Inventory inventory,
                                        final Optional<Promotion> optionalPromotion) {
        if (optionalPromotion.isEmpty()) { // 프로모션 없이 구매
            inventory.buy(quantity);
            return Response.buyWithNoPromotion(RESPONSE_STATUS.BUY_WITH_NO_PROMOTION);
        }
        return null;
    }

    private static void totalOutOfStock(final int quantity, final int totalStock) {
        if (totalStock < quantity) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private static void notExistProductName(final Inventories sameProductInventories) {
        if (sameProductInventories.getInventories().size() == 0) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
    }
}

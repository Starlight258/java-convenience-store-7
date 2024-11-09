package store.domain.system;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.receipt.Receipt;
import store.response.Response;

public class PaymentSystem {

    public static final String NULL = "null";

    private final Inventories inventories;
    private final Promotions promotions;

    public PaymentSystem(final Inventories inventories, final Promotions promotions) {
        this.inventories = inventories;
        this.promotions = promotions;
    }

    public Response canBuy(final String productName, final int quantity,
                           final Receipt receipt, final LocalDate now, Membership membership) {
        Inventories sameProductInventories = inventories.findProducts(productName);
        int totalQuantity = quantity;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Inventory inventory : sameProductInventories.getInventories()) {
            String promotionName = inventory.getPromotionName();
            // 프로모션이 없을 경우 그냥 구매
            if (promotionName.equals(NULL)) {
                inventory.subtract(totalQuantity);
                Product product = inventory.getProduct();
                membership.addNoPromotionProduct(product, totalQuantity);
                receipt.purchaseProducts(product, totalQuantity);
                return Response.buyWithNoPromotion(inventory);
            }
            Optional<Promotion> optionalPromotion = promotions.find(promotionName, now); // 프로모션 찾기
            if (optionalPromotion.isEmpty()) {
                continue;
            }
            Promotion promotion = optionalPromotion.get();

            int stock = inventory.getQuantity();
            int purchaseQuantity = promotion.getPurchaseQuantity();
            int bonusQuantity = promotion.getBonusQuantity();
            int promotionQuantity = purchaseQuantity + bonusQuantity;

            // 재고 이상으로 구매하려고할 때
            if (stock < totalQuantity) {
                // 일부 프로모션만 적용될 경우
                if (promotionQuantity <= stock) {
                    int setSize = stock / (promotionQuantity);
                    int totalBonusQuantity = setSize * bonusQuantity;
                    int noPromotionQuantity = totalQuantity - setSize * (promotionQuantity);
                    return Response.outOfStock(totalBonusQuantity, noPromotionQuantity, inventory);
                }

                // 프로모션 적용 최소수량 만족하지 않으면 그냥 구매
                int inventoryQuantity = inventory.getQuantity();
                totalQuantity -= inventoryQuantity;
                inventories.subtract(inventory, inventoryQuantity);
                totalPrice = totalPrice.add(inventory.calculatePrice(inventoryQuantity));
                Product product = inventory.getProduct();
                receipt.purchaseProducts(product, inventoryQuantity);
                membership.addNoPromotionProduct(product, inventoryQuantity);
                if (totalQuantity == 0) {
                    return Response.buyWithNoPromotion(inventory);
                }
                continue;
            }

            // 할인 적용X
            if (quantity < purchaseQuantity) {
                inventory.subtract(quantity);
                Product product = inventory.getProduct();
                receipt.purchaseProducts(product, quantity);
                membership.addNoPromotionProduct(product, quantity);
                return Response.buyWithNoPromotion(inventory);
            }

            // 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 그 수량만큼 **추가 여부를 입력**받는다.
            // 최소 구매 수량보다는 크고 purchaseQuantity + bonusQuantity이하로 가져온 경우
            if (quantity < promotionQuantity) {
                int freeQuantity = purchaseQuantity + bonusQuantity - quantity;
                return Response.canGetMoreQuantity(bonusQuantity, freeQuantity, inventory);
            }

            // 프로모션이 자동 적용된다.
            int setSize = quantity / (promotionQuantity);
            int totalBonusQuantity = setSize * bonusQuantity; // 보너스 수량
            inventory.subtract(quantity);
            receipt.purchaseProducts(inventory.getProduct(), quantity);
            return Response.buyWithPromotion(totalBonusQuantity, inventory);
        }
        throw new IllegalStateException("[ERROR] 상품을 구매할 수 없습니다.");
    }
}

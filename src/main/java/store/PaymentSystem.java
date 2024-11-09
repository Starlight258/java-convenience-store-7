package store;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class PaymentSystem {

    private final Inventories inventories;
    private final Promotions promotions;

    public PaymentSystem(final Inventories inventories, final Promotions promotions) {
        this.inventories = inventories;
        this.promotions = promotions;
    }

    public Response canBuy(final Inventories sameProductInventories, final int quantity, final LocalDate now,
                           final Map<Product, Integer> purchasedProducts) {
        for (Inventory inventory : sameProductInventories.getInventories()) {
            String promotionName = inventory.getPromotionName();
            Optional<Promotion> optionalPromotion = promotions.find(promotionName); // 프로모션 찾기
            if (optionalPromotion.isEmpty()) {
                return buyWithNoPromotions(quantity, inventory, purchasedProducts);
            }
            // 할인 기간 판단
            Promotion promotion = getPromotion(now, optionalPromotion.get());
            if (promotion == null) {
                continue;
            }
            int purchaseQuantity = promotion.getPurchaseQuantity();
            int bonusQuantity = promotion.getBonusQuantity();
            int stock = inventory.getQuantity(); // 재고
            Response noPromotionQuantity = outOfStock(inventory, quantity, purchaseQuantity, bonusQuantity, stock,
                    sameProductInventories, purchasedProducts);
            if (noPromotionQuantity != null) {
                return noPromotionQuantity;
            }
            // 할인 적용X
            Response noPromotion = buyWithNoPromotion(quantity, inventory, purchaseQuantity, purchasedProducts);
            if (noPromotion != null) {
                return noPromotion;
            }
            // 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 그 수량만큼 **추가 여부를 입력**받는다.
            Response canGetBonus = canGetBonus(quantity, purchaseQuantity, bonusQuantity, inventory);
            if (canGetBonus != null) {
                return canGetBonus;
            }
            // 프로모션이 자동 적용된다.
            Response autoPromotion = autoPromotion(quantity, inventory, purchaseQuantity, bonusQuantity,
                    purchasedProducts);
            if (autoPromotion != null) {
                return autoPromotion;
            }
        }
        throw new IllegalStateException("[ERROR] 상품을 구매할 수 없습니다.");
    }

    public BigDecimal checkMembership(final String line, final Map<String, BigDecimal> totalNoPromotionPrice) {
        BigDecimal membershipPrice = BigDecimal.ZERO;
        if (line.equals("Y")) {
            for (Entry<String, BigDecimal> entry : totalNoPromotionPrice.entrySet()) {
                membershipPrice = membershipPrice.add(entry.getValue());
            }
            membershipPrice = membershipPrice.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(30));
        }
        if (membershipPrice.compareTo(BigDecimal.valueOf(8000)) > 0) {
            return BigDecimal.valueOf(8000);
        }
        return membershipPrice;
    }

    private Response buyWithNoPromotions(final int quantity, final Inventory inventory,
                                         Map<Product, Integer> purchasedProducts) {
        inventory.buy(quantity);
        BigDecimal price = inventory.calculatePrice(quantity);
        purchasedProducts.put(inventory.getProduct(),
                purchasedProducts.getOrDefault(inventory.getProduct(), 0) + quantity);
        return Response.buyWithNoPromotion(price, inventory);
    }

    private Response autoPromotion(final int quantity, final Inventory inventory, final int purchaseQuantity,
                                   final int bonusQuantity, final Map<Product, Integer> purchasedProducts) {
        if (quantity > purchaseQuantity) {
            int setSize = quantity / (purchaseQuantity + bonusQuantity);
            int totalBonusQuantity = setSize * bonusQuantity; // 보너스 수량
            inventory.buy(quantity);
            purchasedProducts.put(inventory.getProduct(),
                    purchasedProducts.getOrDefault(inventory.getProduct(), 0) + quantity);
            return Response.buyWithPromotion(totalBonusQuantity, inventory);
        }
        return null;
    }

    private static Response canGetBonus(final int quantity, final int purchaseQuantity, final int bonusQuantity,
                                        final Inventory inventory) {
        if (quantity < purchaseQuantity + bonusQuantity) {
            int freeQuantity = purchaseQuantity + bonusQuantity - quantity;
            return Response.canGetMoreQuantity(bonusQuantity, freeQuantity, inventory);
        }
        return null;
    }

    private static Response buyWithNoPromotion(final int quantity, final Inventory inventory,
                                               final int purchaseQuantity,
                                               final Map<Product, Integer> purchasedProducts) {
        if (quantity < purchaseQuantity) {
            inventory.buy(quantity);
            BigDecimal totalPrice = inventory.calculatePrice(quantity);
            purchasedProducts.put(inventory.getProduct(),
                    purchasedProducts.getOrDefault(inventory.getProduct(), 0) + quantity);
            return Response.buyWithNoPromotion(totalPrice, inventory);
        }
        return null;
    }

    private static Response outOfStock(final Inventory inputInventory, final int quantity, final int purchaseQuantity,
                                       final int bonusQuantity,
                                       final int stock, final Inventories inventories,
                                       final Map<Product, Integer> purchasedProducts) {
        if (stock < quantity) {
            if (stock >= purchaseQuantity + bonusQuantity
                    && purchaseQuantity + bonusQuantity < quantity) { // 일부 프로모션 적용
                int setSize = stock / (purchaseQuantity + bonusQuantity);
                int totalBonusQuantity = setSize * bonusQuantity;
                int noPromotionQuantity = quantity - setSize * (purchaseQuantity + bonusQuantity);
                return Response.outOfStock(totalBonusQuantity, noPromotionQuantity, inputInventory);
            }
            // 프로모션 적용 최소수량 만족X 프로모션 재고 초과 - > 그냥 구매
            int totalQuantity = quantity;
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (Inventory inventory : inventories.getInventories()) {
                if (inventory.hasPromotion()) {
                    int inventoryQuantity = inventory.getQuantity();
                    totalQuantity -= inventoryQuantity;
                    inventory.buy(inventoryQuantity); // 전체 사용
                    totalPrice = totalPrice.add(inventory.calculatePrice(inventoryQuantity));
                    Product product = inventory.getProduct();
                    purchasedProducts.put(product, purchasedProducts.getOrDefault(product, 0) + inventoryQuantity);
                    continue;
                }
                inventory.buy(totalQuantity);
                totalPrice = totalPrice.add(inventory.calculatePrice(totalQuantity));
                Product product = inventory.getProduct();
                purchasedProducts.put(product, purchasedProducts.getOrDefault(product, 0) + totalQuantity);
            }
            return Response.buyWithNoPromotion(totalPrice, inputInventory);
        }
        return null;
    }

    private static Promotion getPromotion(final LocalDate now, final Promotion promotion) {
        if (!promotion.isPromotionPeriod(now)) {
            return null;
        }
        return promotion;
    }
}

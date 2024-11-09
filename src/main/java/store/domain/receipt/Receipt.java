package store.domain.receipt;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.inventory.Product;

public class Receipt {

    private final Map<Product, Integer> purchasedProducts;
    private final Map<Product, Integer> bonusProducts;
    private final BigDecimal memberShipDiscountPrice;

    public Receipt(final Map<Product, Integer> purchasedProducts, final Map<Product, Integer> bonusProducts,
                   final BigDecimal memberShipDiscountPrice) {
        this.purchasedProducts = purchasedProducts;
        this.bonusProducts = bonusProducts;
        this.memberShipDiscountPrice = memberShipDiscountPrice;
    }

    public Map<Product, Integer> getPurchasedProducts() {
        return Collections.unmodifiableMap(purchasedProducts);
    }

    public Map<Product, Integer> getBonusProducts() {
        return Collections.unmodifiableMap(bonusProducts);
    }


    public Map.Entry<Integer, BigDecimal> getTotalPurchase() {
        int totalCount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Entry<Product, Integer> entry : purchasedProducts.entrySet()) {
            totalCount += entry.getValue();
            totalPrice = totalPrice.add(entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return Map.entry(totalCount, totalPrice);
    }

    public BigDecimal getPromotionDiscountPrice() {
        BigDecimal promotionPrice = BigDecimal.ZERO;
        for (Entry<Product, Integer> entry : bonusProducts.entrySet()) {
            promotionPrice = promotionPrice.add(
                    entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return promotionPrice;
    }

    public BigDecimal getMemberShipDiscountPrice() {
        return memberShipDiscountPrice;
    }

    public BigDecimal getPriceToPay() {
        BigDecimal price = getTotalPurchase().getValue();
        price = price.subtract(getPromotionDiscountPrice());
        price = price.subtract(getMemberShipDiscountPrice());
        return price;
    }
}

package store.domain.receipt;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;

@DisplayName("영수증 테스트")
class ReceiptTest {
    private final Product coke = new Product("콜라", new BigDecimal(1000));
    private final Quantity quantity = new Quantity(2);
    private final Price price = new Price(new BigDecimal(2000));

    @Test
    @DisplayName("영수증에 상품을 추가한다")
    void 상품추가() {
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        receipt.purchaseProducts(coke, quantity);
        assertThat(receipt).extracting("purchasedProducts").isEqualTo(new HashMap<>() {{
            put(coke, quantity);
        }});
    }

    @Test
    @DisplayName("영수증에 보너스 상품을 추가한다")
    void 보너스상품() {
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        receipt.addBonusProducts(coke, quantity);
        assertThat(receipt).extracting("bonusProducts").isEqualTo(new HashMap<>() {{
            put(coke, quantity);
        }});
    }

    @Test
    @DisplayName("보너스 상품 가격 합을 구한다.")
    void 성공_보너스상품가격합() {
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>() {{
            put(new Product("콜라", new BigDecimal(1000)), new Quantity(2));
        }});
        Price price = receipt.getPromotionDiscountPrice();
        assertThat(price.getPrice()).isEqualTo(new BigDecimal(2000));
    }
}

package store.domain.receipt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;

@DisplayName("영수증 테스트")
class ReceiptTest {

    @Test
    @DisplayName("구매한 상품을 추가한다.")
    void 성공_구매상품추가() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Product coke = new Product("콜라", new BigDecimal(1000));

        // When
        receipt.purchaseProducts(coke, new Quantity(2));

        // Then
        assertThat(receipt).extracting("purchasedProducts").isEqualTo(new HashMap<>() {{
            put(coke, new Quantity(2));
        }});
    }

    @Test
    @DisplayName("보너스 상품을 추가한다")
    void 성공_보너스상품추가() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Product coke = new Product("콜라", new BigDecimal(1000));

        // When
        receipt.addBonusProducts(coke, new Quantity(2));

        // Then
        assertThat(receipt).extracting("bonusProducts").isEqualTo(new HashMap<>() {{
            put(coke, new Quantity(2));
        }});
    }

    @Test
    @DisplayName("보너스 상품 가격 합을 구한다.")
    void 성공_보너스상품가격합() {
        // Given
        Product coke = new Product("콜라", new BigDecimal(1000));
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>() {{
            put(coke, new Quantity(2));
        }});

        // When
        Price price = receipt.getPromotionDiscountPrice();

        // Then
        assertThat(price.getPrice()).isEqualTo(new BigDecimal(2000));
    }

    @Test
    @DisplayName("내실 돈을 계산한다.")
    void 성공_내실돈계산() {
        // Given
        Product coke = new Product("콜라", new BigDecimal(1000));
        Receipt receipt = new Receipt(new HashMap<>() {{
            put(coke, new Quantity(2));
        }}, new HashMap<>());

        // When
        Price price = receipt.getPriceToPay(new Price(new BigDecimal(2000)), Price.zero());

        // Then
        assertThat(price.getPrice()).isEqualTo(new BigDecimal(2000));
    }

    @Test
    @DisplayName("총 금액을 구한다.")
    void 성공_총금액계산() {
        // Given
        Product coke = new Product("콜라", new BigDecimal(1000));
        Receipt receipt = new Receipt(new HashMap<>() {{
            put(coke, new Quantity(2));
        }}, new HashMap<>());

        // When
        Entry<Quantity, Price> totalPurchase = receipt.getTotalPurchase();

        // Then
        assertAll(
                () -> assertThat(totalPurchase.getKey()).isEqualTo(new Quantity(2)),
                () -> assertThat(totalPurchase.getValue()).isEqualTo(new Price(new BigDecimal(2000)))
        );
    }
}

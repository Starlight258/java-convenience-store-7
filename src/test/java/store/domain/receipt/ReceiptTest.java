package store.domain.receipt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.inventory.Product;

@DisplayName("영수증 테스트")
class ReceiptTest {

    @Test
    @DisplayName("구매한 상품을 추가한다.")
    void 성공_구매상품추가() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Product coke = new Product("콜라", new BigDecimal(1000));

        // When
        receipt.purchaseProducts(coke, 2);

        // Then
        assertThat(receipt).extracting("purchasedProducts").isEqualTo(new HashMap<>() {{
            put(coke, 2);
        }});
    }

    @Test
    @DisplayName("보너스 상품을 추가한다")
    void 성공_보너스상품추가() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Product coke = new Product("콜라", new BigDecimal(1000));

        // When
        receipt.addBonusProducts(coke, 2);

        // Then
        assertThat(receipt).extracting("bonusProducts").isEqualTo(new HashMap<>() {{
            put(coke, 2);
        }});
    }

    @Test
    @DisplayName("보너스 상품 가격 합을 구한다.")
    void 성공_보너스상품가격합() {
        // Given
        Product coke = new Product("콜라", new BigDecimal(1000));
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>() {{
            put(coke, 2);
        }});

        // When
        BigDecimal price = receipt.getPromotionDiscountPrice();

        // Then
        assertThat(price).isEqualTo(new BigDecimal(2000));
    }

    @Test
    @DisplayName("내실 돈을 계산한다.")
    void 성공_내실돈계산() {
        // Given
        Product coke = new Product("콜라", new BigDecimal(1000));
        Receipt receipt = new Receipt(new HashMap<>() {{
            put(coke, 2);
        }}, new HashMap<>());

        // When
        BigDecimal price = receipt.getPriceToPay(new BigDecimal(2000), BigDecimal.ZERO);

        // Then
        assertThat(price).isEqualTo(new BigDecimal(2000));
    }

    @Test
    @DisplayName("총 금액을 구한다.")
    void 성공_총금액계산() {
        // Given
        Product coke = new Product("콜라", new BigDecimal(1000));
        Receipt receipt = new Receipt(new HashMap<>() {{
            put(coke, 2);
        }}, new HashMap<>());

        // When
        Entry<Integer, BigDecimal> totalPurchase = receipt.getTotalPurchase();

        // Then
        assertAll(
                () -> assertThat(totalPurchase.getKey()).isEqualTo(2),
                () -> assertThat(totalPurchase.getValue()).isEqualTo(new BigDecimal(2000))
        );
    }
}

package store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("영수증 테스트")
public class ReceiptTest {

    @Test
    @DisplayName("영수증을 생성한다.")
    void 성공_영수증생성() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(2500));
        String promotionName = "탄산2+1";
        Inventory cokeWithPromotion = new Inventory(coke, 10, promotionName);
        Inventory cokeWithNoPromotion = new Inventory(coke, 10, "null");
        Inventory juiceWithNoPromotion = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(
                List.of(cokeWithPromotion, cokeWithNoPromotion, juiceWithNoPromotion));
        Map<Product, Integer> purchasedItems = new HashMap<>() {{
            put(coke, 10);
            put(juice, 3);
        }};
        Map<Product, Integer> bonusItems = new HashMap<>() {{
            put(coke, 3);
        }};
        BigDecimal membershipDiscountPrice = BigDecimal.valueOf(2250);

        // When & Then
        assertThatCode(() -> {
            new Receipt(purchasedItems, bonusItems, membershipDiscountPrice);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구매 내역을 출력한다.")
    void 성공_구매내역출력() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(2500));
        String promotionName = "탄산2+1";
        Inventory cokeWithPromotion = new Inventory(coke, 10, promotionName);
        Inventory cokeWithNoPromotion = new Inventory(coke, 10, "null");
        Inventory juiceWithNoPromotion = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(
                List.of(cokeWithPromotion, cokeWithNoPromotion, juiceWithNoPromotion));
        Map<Product, Integer> purchasedItems = new HashMap<>() {{
            put(coke, 10);
            put(juice, 3);
        }};
        Map<Product, Integer> bonusItems = new HashMap<>() {{
            put(coke, 3);
        }};
        BigDecimal membershipDiscountPrice = BigDecimal.valueOf(2250);
        Receipt receipt = new Receipt(purchasedItems, bonusItems, membershipDiscountPrice);

        // When
        Map<Product, Integer> purchasedItems1 = receipt.getPurchasedProducts();

        // Then
        assertThat(purchasedItems1).containsEntry(coke, 10)
                .containsEntry(juice, 3);
    }

    @Test
    @DisplayName("증정 내역을 출력한다.")
    void 성공_증정내역출력() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(2500));
        String promotionName = "탄산2+1";
        Inventory cokeWithPromotion = new Inventory(coke, 10, promotionName);
        Inventory cokeWithNoPromotion = new Inventory(coke, 10, "null");
        Inventory juiceWithNoPromotion = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(
                List.of(cokeWithPromotion, cokeWithNoPromotion, juiceWithNoPromotion));
        Map<Product, Integer> purchasedItems = new HashMap<>() {{
            put(coke, 10);
            put(juice, 3);
        }};
        Map<Product, Integer> bonusItems = new HashMap<>() {{
            put(coke, 3);
        }};
        BigDecimal membershipDiscountPrice = BigDecimal.valueOf(2250);
        Receipt receipt = new Receipt(purchasedItems, bonusItems, membershipDiscountPrice);

        // When
        Map<Product, Integer> bonusItems1 = receipt.getBonusProducts();

        // Then
        assertThat(bonusItems1).containsEntry(coke, 3);
    }

    @Test
    @DisplayName("총 구매액을 출력한다.")
    void 성공_총구매액출력() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(2500));
        String promotionName = "탄산2+1";
        Inventory cokeWithPromotion = new Inventory(coke, 10, promotionName);
        Inventory cokeWithNoPromotion = new Inventory(coke, 10, "null");
        Inventory juiceWithNoPromotion = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(
                List.of(cokeWithPromotion, cokeWithNoPromotion, juiceWithNoPromotion));
        Map<Product, Integer> purchasedItems = new HashMap<>() {{
            put(coke, 10);
            put(juice, 3);
        }};
        Map<Product, Integer> bonusItems = new HashMap<>() {{
            put(coke, 3);
        }};
        BigDecimal membershipDiscountPrice = BigDecimal.valueOf(2250);
        Receipt receipt = new Receipt(purchasedItems, bonusItems, membershipDiscountPrice);

        // When
        Entry<Integer, BigDecimal> entry = receipt.getTotalPurchase();

        // Then
        assertAll(
                () -> assertThat(entry.getKey()).isEqualTo(13),
                () -> assertThat(entry.getValue()).isEqualTo(BigDecimal.valueOf(17500))
        );
    }

    @Test
    @DisplayName("행사 할인을 출력한다.")
    void 성공_행사할인() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(2500));
        String promotionName = "탄산2+1";
        Inventory cokeWithPromotion = new Inventory(coke, 10, promotionName);
        Inventory cokeWithNoPromotion = new Inventory(coke, 10, "null");
        Inventory juiceWithNoPromotion = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(
                List.of(cokeWithPromotion, cokeWithNoPromotion, juiceWithNoPromotion));
        Map<Product, Integer> purchasedItems = new HashMap<>() {{
            put(coke, 10);
            put(juice, 3);
        }};
        Map<Product, Integer> bonusItems = new HashMap<>() {{
            put(coke, 3);
        }};
        BigDecimal membershipDiscountPrice = BigDecimal.valueOf(2250);
        Receipt receipt = new Receipt(purchasedItems, bonusItems, membershipDiscountPrice);

        // When
        BigDecimal promotionDiscountPrice = receipt.getPromotionDiscountPrice();

        // Then
        assertThat(promotionDiscountPrice).isEqualTo(BigDecimal.valueOf(3000));
    }

    @Test
    @DisplayName("멤버십 할인액을 출력한다.")
    void 성공_멤버십할인() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(2500));
        String promotionName = "탄산2+1";
        Inventory cokeWithPromotion = new Inventory(coke, 10, promotionName);
        Inventory cokeWithNoPromotion = new Inventory(coke, 10, "null");
        Inventory juiceWithNoPromotion = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(
                List.of(cokeWithPromotion, cokeWithNoPromotion, juiceWithNoPromotion));
        Map<Product, Integer> purchasedItems = new HashMap<>() {{
            put(coke, 10);
            put(juice, 3);
        }};
        Map<Product, Integer> bonusItems = new HashMap<>() {{
            put(coke, 3);
        }};
        BigDecimal membershipDiscountPrice = BigDecimal.valueOf(2250);
        Receipt receipt = new Receipt(purchasedItems, bonusItems, membershipDiscountPrice);

        // When
        BigDecimal promotionDiscountPrice = receipt.getMemberShipDiscountPrice();

        // Then
        assertThat(promotionDiscountPrice).isEqualTo(BigDecimal.valueOf(2250));
    }

    @Test
    @DisplayName("내실돈을 출력한다.")
    void 성공_내실돈() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(2500));
        String promotionName = "탄산2+1";
        Inventory cokeWithPromotion = new Inventory(coke, 10, promotionName);
        Inventory cokeWithNoPromotion = new Inventory(coke, 10, "null");
        Inventory juiceWithNoPromotion = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(
                List.of(cokeWithPromotion, cokeWithNoPromotion, juiceWithNoPromotion));
        Map<Product, Integer> purchasedItems = new HashMap<>() {{
            put(coke, 10);
            put(juice, 3);
        }};
        Map<Product, Integer> bonusItems = new HashMap<>() {{
            put(coke, 3);
        }};
        BigDecimal membershipDiscountPrice = BigDecimal.valueOf(2250);
        Receipt receipt = new Receipt(purchasedItems, bonusItems, membershipDiscountPrice);

        // When
        BigDecimal priceToPay = receipt.getPriceToPay();

        // Then
        assertThat(priceToPay).isEqualTo(BigDecimal.valueOf(12250));
    }
}

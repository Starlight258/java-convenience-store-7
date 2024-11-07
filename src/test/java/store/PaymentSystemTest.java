package store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("결제 시스템 테스트")
public class PaymentSystemTest {

    @Test
    @DisplayName("결제 시스템 테스트")
    void 성공_생성() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventoryWithPromotion = new Inventory(product, 10, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));

        // When & Then
        assertThatCode(() -> {
            new PaymentSystem(inventories, promotions);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상품이 없을 경우 예외가 발생한다")
    void 실패_안내_상품없음() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventoryWithPromotion = new Inventory(product, 10, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When & Then
        assertThatThrownBy(() -> paymentSystem.canBuy("냠냠", 3, now))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("존재하지 않는 상품입니다. 다시 입력해 주세요.")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("재고 수량 이상으로 구매할 경우 예외가 발생한다")
    void 실패_안내_재고없음() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventoryWithPromotion = new Inventory(product, 10, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);

        // When & Then
        assertThatThrownBy(() -> paymentSystem.canBuy("coke", 23, LocalDate.now()))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("프로모션 기간이 지난 경우 해당 프로모션을 적용하지 않고 넘어간다.")
    void 성공_안내_프로모션기간고려() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventoryWithPromotion = new Inventory(product, 10, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2025, 1, 1);

        // When
        paymentSystem.canBuy("coke", 3, now);

        // Then
        assertAll(
                () -> assertThat(inventoryWithPromotion).extracting("quantity").isEqualTo(10),
                () -> assertThat(inventoryWithNoPromotion).extracting("quantity").isEqualTo(7)
        );
    }

    @Test
    @DisplayName("프로모션 기간임에도 최소 수량을 만족하지 못하면 해당 프로모션을 적용하지 않고 넘어간다.")
    void 성공_안내_프로모션기간임에도조건에해당X() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventoryWithPromotion = new Inventory(product, 10, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2025, 1, 1);

        // When
        Response response = paymentSystem.canBuy("coke", 1, now);

        // Then
        assertAll(
                () -> assertThat(inventoryWithPromotion).extracting("quantity").isEqualTo(10),
                () -> assertThat(inventoryWithNoPromotion).extracting("quantity").isEqualTo(9)
        );
    }

    @Test
    @DisplayName("프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제하게 됨을 안내한다.")
    void 성공_안내_프로모션재고부족() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventoryWithPromotion = new Inventory(product, 7, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When
        Response response = paymentSystem.canBuy("coke", 10, now);

        // Then
        assertAll(
                () -> assertThat(response).extracting("status").isEqualTo(RESPONSE_STATUS.OUT_OF_STOCK),
                () -> assertThat(response).extracting("noPromotionQuantity").isEqualTo(4)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("프로모션 가능 상품에 대해 고객이 해당 수량보다 적게 가져온 경우 보너스 수량에 대해 안내한다.")
    void 성공_안내_적게가져옴(int productBuyQuantity, int productBonusQuantity, int quantity, int bonusQuantity) {
        // Given
        String productName = "coke";
        Product product = new Product(productName, BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산프로모션";
        Promotion cokePromotion = new Promotion(promotionName, productBuyQuantity, productBonusQuantity, startDate,
                endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventory = new Inventory(product, 10, promotionName);
        Inventories inventories = new Inventories(List.of(inventory));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When
        Response response = paymentSystem.canBuy(productName, quantity, now);

        // Then
        assertAll(
                () -> assertThat(response.status()).isEqualTo(RESPONSE_STATUS.CAN_GET_BONUS),
                () -> assertThat(response.bonusQuantity()).isEqualTo(bonusQuantity)
        );
    }

    // 2+1 일때 1개살경우?
    private static Stream<Arguments> 성공_안내_적게가져옴() {
        return Stream.of(
                Arguments.of(2, 1, 2, 1),
                Arguments.of(2, 2, 2, 2),
                Arguments.of(2, 2, 3, 1)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("프로모션 가능 상품에 대해 고객이 해당 수량이상으로 가져올 경우 프로모션이 적용되어 구매된다.")
    void 성공_안내_프로모션적용(int productBuyQuantity, int productBonusQuantity, int quantity, int totalBonusQuantity) {
        // Given
        String productName = "coke";
        Product product = new Product(productName, BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산프로모션";
        Promotion cokePromotion = new Promotion(promotionName, productBuyQuantity, productBonusQuantity, startDate,
                endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventory = new Inventory(product, 10, promotionName);
        Inventories inventories = new Inventories(List.of(inventory));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When
        Response response = paymentSystem.canBuy(productName, quantity, now);

        // Then
        assertAll(
                () -> assertThat(response.status()).isEqualTo(RESPONSE_STATUS.BUY),
                () -> assertThat(response.bonusQuantity()).isEqualTo(totalBonusQuantity)
        );
    }

    private static Stream<Arguments> 성공_안내_프로모션적용() {
        return Stream.of(
                Arguments.of(2, 1, 3, 1),
                Arguments.of(2, 2, 4, 2),
                Arguments.of(2, 2, 5, 2)
        );
    }
}

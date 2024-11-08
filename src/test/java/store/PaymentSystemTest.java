package store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("결제 시스템 테스트")
public class PaymentSystemTest {

    @Test
    @DisplayName("생성에 성공한다.")
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
        Response response = paymentSystem.canBuy("coke", 3, now);

        // Then
        assertAll(
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(response.totalPrice()).isEqualTo(BigDecimal.valueOf(3000)),
                () -> assertThat(inventoryWithPromotion).extracting("quantity").isEqualTo(10),
                () -> assertThat(inventoryWithNoPromotion).extracting("quantity").isEqualTo(7)
        );
    }

    @Test
    @DisplayName("프로모션이 없을 경우 프로모션을 적용하지 않는다.")
    void 성공_안내_프로모션X() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, new Promotions(Collections.emptyList()));
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When
        Response response = paymentSystem.canBuy("coke", 3, now);

        // Then
        assertAll(
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(response.totalPrice()).isEqualTo(BigDecimal.valueOf(3000)),
                () -> assertThat(inventoryWithNoPromotion).extracting("quantity").isEqualTo(7)
        );
    }

    @Test
    @DisplayName("적용가능한 프로모션의 재고가 없을 경우 일반재고에서 함께 구매한다.")
    void 성공_안내_일반재고() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory inventoryWithPromotion = new Inventory(product, 1, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When
        Response response = paymentSystem.canBuy("coke", 3, now);

        // Then
        assertAll(
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(response.totalPrice()).isEqualTo(BigDecimal.valueOf(3000)),
                () -> assertThat(inventoryWithPromotion).extracting("quantity").isEqualTo(0),
                () -> assertThat(inventoryWithNoPromotion).extracting("quantity").isEqualTo(8)
        );
    }

    @Test
    @DisplayName("프로모션 기간임에도 최소 구매 수량을 만족하지 못하면 프로모션 재고만 줄인다.")
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
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When
        Response response = paymentSystem.canBuy("coke", 1, now);

        // Then
        assertAll(
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(response.totalPrice()).isEqualTo(BigDecimal.valueOf(1000)),
                () -> assertThat(inventoryWithPromotion).extracting("quantity").isEqualTo(9),
                () -> assertThat(inventoryWithNoPromotion).extracting("quantity").isEqualTo(10)
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
                () -> assertThat(response).extracting("status").isEqualTo(ResponseStatus.OUT_OF_STOCK),
                () -> assertThat(response.bonusQuantity()).isEqualTo(2),
                () -> assertThat(response).extracting("noPromotionQuantity").isEqualTo(4)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("프로모션 가능 상품에 대해 고객이 해당 수량보다 적게 가져온 경우 보너스 수량에 대해 안내한다.")
    void 성공_안내_적게가져옴(int productBuyQuantity, int productBonusQuantity, int quantity, int bonusQuantity,
                     int canGetMoreQuantity) {
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
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.CAN_GET_BONUS),
                () -> assertThat(response.bonusQuantity()).isEqualTo(bonusQuantity),
                () -> assertThat(response.canGetMoreQuantity()).isEqualTo(canGetMoreQuantity)
        );
    }

    // 2+1 일때 1개살경우?
    private static Stream<Arguments> 성공_안내_적게가져옴() {
        return Stream.of(
                Arguments.of(2, 1, 2, 1, 1),
                Arguments.of(2, 2, 2, 2, 2),
                Arguments.of(2, 2, 3, 2, 1)
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
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_PROMOTION),
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

    @Test
    @DisplayName("멤버십은 프로모션이 적용되지 않은 상품에 대해서만 적용된다.")
    void 성공_안내_멤버십대상상품() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory cokeInventory = new Inventory(coke, 10, promotionName);
        Inventory juiceInventory = new Inventory(juice, 10, null);
        Inventories inventories = new Inventories(List.of(cokeInventory, juiceInventory));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);

        // When
        Response response = paymentSystem.canBuy("juice", 9, now);

        // Then
        assertAll(
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(response.totalPrice()).isEqualTo(BigDecimal.valueOf(9000)),
                () -> assertThat(cokeInventory).extracting("quantity").isEqualTo(10),
                () -> assertThat(juiceInventory).extracting("quantity").isEqualTo(1)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {240_000, 250_000})
    @DisplayName("멤버십 할인은 최대 8000원까지 가능하다.")
    void 성공_멤버십할인_멤버십가격계산(int price) {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Product juice = new Product("juice", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory cokeInventory = new Inventory(coke, 10, promotionName);
        Inventory juiceInventory = new Inventory(juice, 10, null);
        Inventories inventories = new Inventories(List.of(cokeInventory, juiceInventory));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);
        Map<String, BigDecimal> memberShipPrice = new HashMap<>();
        memberShipPrice.put("juice", BigDecimal.valueOf(price));

        // When
        BigDecimal totalMembershipPrice = paymentSystem.checkMembership("Y", memberShipPrice);
        // Then
        assertThat(totalMembershipPrice).isEqualTo(BigDecimal.valueOf(8000));
    }
}

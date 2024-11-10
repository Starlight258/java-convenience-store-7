package store.domain.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.receipt.Receipt;
import store.response.Response;
import store.response.ResponseStatus;

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
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, "null");
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));

        // When & Then
        assertThatCode(() -> {
            new PaymentSystem(inventories, promotions);
        }).doesNotThrowAnyException();
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
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, "null");
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2025, 1, 1);
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("coke", 3, store, now);

        // Then
        assertAll(
                () -> assertThat(membership).extracting("noPromotionProducts").isEqualTo(Map.of(product, 3)),
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of(product, 3)),
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(membership.calculateDiscount()).isEqualTo(BigDecimal.valueOf(900)),
                () -> assertThat(inventoryWithPromotion).extracting("quantity").isEqualTo(10),
                () -> assertThat(inventoryWithNoPromotion).extracting("quantity").isEqualTo(7)
        );
    }

    @Test
    @DisplayName("프로모션이 없을 경우 프로모션을 적용하지 않는다.")
    void 성공_안내_프로모션X() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, "null");
        Inventories inventories = new Inventories(List.of(inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, new Promotions(Collections.emptyList()));
        LocalDate now = LocalDate.of(2024, 3, 1);
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("coke", 3, store, now);

        // Then
        assertAll(
                () -> assertThat(membership).extracting("noPromotionProducts").isEqualTo(Map.of(product, 3)),
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of(product, 3)),
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(membership.calculateDiscount()).isEqualTo(BigDecimal.valueOf(900)),
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
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, "null");
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("coke", 3, store, now);

        // Then
        assertAll(
                () -> assertThat(membership).extracting("noPromotionProducts").isEqualTo(Map.of(product, 3)),
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of(product, 3)),
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(membership.calculateDiscount()).isEqualTo(BigDecimal.valueOf(900)),
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
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, "null");
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("coke", 1, store, now);

        // Then
        assertAll(
                () -> assertThat(membership).extracting("noPromotionProducts").isEqualTo(Map.of(product, 1)),
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of(product, 1)),
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(membership.calculateDiscount()).isEqualTo(BigDecimal.valueOf(300)),
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
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, "null");
        Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("coke", 10, store, now);

        // Then
        assertAll(
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of()),
                // TODO: 일부 수량이 프로모션 받지 못하는 경우에 대해 Y인 경우도 테스트
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
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("coke", quantity, store, now);

        // Then
        assertAll(
                // TODO: 보너스 수량에 대해 Y인 경우도 테스트
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of()),
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
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("coke", quantity, store, now);

        // Then
        assertAll(
                () -> assertThat(membership).extracting("noPromotionProducts").isEqualTo(Map.of()),
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of(product, quantity)),
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_PROMOTION),
                () -> assertThat(response.bonusQuantity()).isEqualTo(totalBonusQuantity),
                () -> assertThat(membership.calculateDiscount()).isEqualTo(BigDecimal.ZERO)
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
        Product juice = new Product("juice", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String promotionName = "탄산2+1";
        Promotion cokePromotion = new Promotion(promotionName, 2, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion));
        Inventory juiceInventory = new Inventory(juice, 10, "null");
        Inventories inventories = new Inventories(List.of(juiceInventory));
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = LocalDate.of(2024, 3, 1);
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);

        // When
        Response response = paymentSystem.canBuy("juice", 9, store, now);

        // Then
        assertAll(
                () -> assertThat(membership).extracting("noPromotionProducts").isEqualTo(Map.of(juice, 9)),
                () -> assertThat(receipt).extracting("purchasedProducts").isEqualTo(Map.of(juice, 9)),
                () -> assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION),
                () -> assertThat(membership.calculateDiscount()).isEqualTo(BigDecimal.valueOf(2700)),
                () -> assertThat(juiceInventory).extracting("quantity").isEqualTo(1)
        );
    }
}

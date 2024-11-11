package store.domain.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.response.Response;
import store.response.ResponseStatus;

@DisplayName("결제 시스템 테스트")
public class PaymentSystemTest {

    private static final String PRODUCT_NAME = "coke";
    private static final Product PRODUCT = new Product(PRODUCT_NAME, BigDecimal.valueOf(1000));

    private Store store;
    private PaymentSystem paymentSystem;

    @BeforeEach
    void setUp() {
        store = new Store(new Receipt(new HashMap<>(), new HashMap<>()), new Membership(new HashMap<>()));
    }

    private PaymentSystem createSystem(int promotionStock, int normalStock) {
        Promotion promotion = new Promotion("탄산2+1", new Quantity(2), new Quantity(1),
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        return new PaymentSystem(
                new Inventories(Arrays.asList(
                        new Inventory(PRODUCT, promotionStock, "탄산2+1"),
                        new Inventory(PRODUCT, normalStock, "null")
                )),
                new Promotions(List.of(promotion)));
    }

    @Test
    @DisplayName("생성에 성공한다")
    void createPaymentSystem() {
        assertThatCode(() -> createSystem(10, 10)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("프로모션 기간이 지난 경우 일반 구매로 처리한다")
    void expiredPromotion() {
        Response response = createSystem(10, 10)
                .canBuy(PRODUCT_NAME, new Quantity(3), store, LocalDate.of(2025, 1, 1));
        assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }

    @Test
    @DisplayName("프로모션이 없는 상품 구매한다")
    void buyWithNoPromotion() {
        paymentSystem = new PaymentSystem(
                new Inventories(List.of(new Inventory(PRODUCT, 10, "null"))),
                new Promotions(Collections.emptyList()));
        assertThat(paymentSystem.canBuy(PRODUCT_NAME, new Quantity(3), store,
                LocalDate.of(2024, 3, 1)).status())
                .isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }

    @Test
    @DisplayName("프로모션 재고가 부족할 때 일부만 적용한다")
    void outOfPromotionStock() {
        Response response = createSystem(7, 10)
                .canBuy(PRODUCT_NAME, new Quantity(10), store, LocalDate.of(2024, 3, 1));
        assertThat(response.status()).isEqualTo(ResponseStatus.OUT_OF_STOCK);
        assertThat(response.bonusQuantity()).isEqualTo(new Quantity(2));
    }

    @Test
    @DisplayName("보너스 수량을 안내한다")
    void notifyAvailableBonus() {
        Response response = createSystem(10, 0)
                .canBuy(PRODUCT_NAME, new Quantity(2), store, LocalDate.of(2024, 3, 1));
        assertThat(response.status()).isEqualTo(ResponseStatus.CAN_GET_BONUS);
        assertThat(response.bonusQuantity()).isEqualTo(new Quantity(1));
    }
}

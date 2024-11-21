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
import store.domain.PurchaseContext;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.order.Orders.Order;
import store.domain.promotion.Promotion;
import store.domain.promotion.PromotionProcessor;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.response.Response;
import store.response.ResponseStatus;

@DisplayName("결제 프로세서 테스트")
public class PromotionProcessorTest {

    private static final Product PRODUCT = new Product("coke", BigDecimal.valueOf(1000));

    private Store store;

    @BeforeEach
    void setUp() {
        store = new Store(new Receipt(new HashMap<>(), new HashMap<>()), new Membership(new HashMap<>()));
    }

    private PromotionProcessor createSystem(int promotionStock, int normalStock) {
        Promotion promotion = new Promotion("탄산2+1", new Quantity(2), new Quantity(1),
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        return new PromotionProcessor(
                new Inventories(Arrays.asList(
                        new Inventory(PRODUCT, promotionStock, "탄산2+1"),
                        new Inventory(PRODUCT, normalStock, "null")
                )),
                new Promotions(List.of(promotion)));
    }

    private Order createOrder(int quantity) {
        return new Order("coke", new Quantity(quantity));
    }

    @Test
    @DisplayName("생성에 성공한다")
    void createPaymentSystem() {
        assertThatCode(() -> createSystem(10, 10)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("프로모션이 없는 상품 구매한다")
    void buyWithNoPromotion() {
        PurchaseContext context = new PurchaseContext();
        PromotionProcessor promotionProcessor = new PromotionProcessor(
                new Inventories(List.of(new Inventory(PRODUCT, 10, "null"))),
                new Promotions(Collections.emptyList()));
        assertThat(promotionProcessor.pay(createOrder(3), store, context).status())
                .isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }

    @Test
    @DisplayName("프로모션 재고가 부족할 때 일부만 적용한다")
    void outOfPromotionStock() {
        PurchaseContext context = new PurchaseContext();
        Response response = createSystem(7, 10)
                .pay(createOrder(10), store, context);
        assertThat(response.status()).isEqualTo(ResponseStatus.OUT_OF_STOCK);
        assertThat(response.bonusQuantity()).isEqualTo(new Quantity(2));
    }

    @Test
    @DisplayName("보너스 수량을 안내한다")
    void notifyAvailableBonus() {
        PurchaseContext context = new PurchaseContext();
        Response response = createSystem(10, 0)
                .pay(createOrder(2), store, context);
        assertThat(response.status()).isEqualTo(ResponseStatus.CAN_GET_BONUS);
        assertThat(response.bonusQuantity()).isEqualTo(new Quantity(1));
    }
}

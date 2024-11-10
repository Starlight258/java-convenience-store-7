package store.domain.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1000);
    private static final String PROMOTION_NAME = "탄산2+1";
    private static final LocalDate VALID_DATE = LocalDate.of(2024, 3, 1);
    private static final LocalDate EXPIRED_DATE = LocalDate.of(2025, 1, 1);

    private Product product;
    private Store store;
    private PaymentSystem paymentSystem;

    @BeforeEach
    void setUp() {
        product = new Product(PRODUCT_NAME, PRODUCT_PRICE);
        store = new Store(new Receipt(new HashMap<>(), new HashMap<>()), new Membership(new HashMap<>()));
    }

    private PaymentSystem createSystem(int promotionStock, int normalStock) {
        Promotion promotion = new Promotion(PROMOTION_NAME, new Quantity(2), new Quantity(1),
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        Inventory withPromotion = new Inventory(product, promotionStock, PROMOTION_NAME);
        Inventory withoutPromotion = new Inventory(product, normalStock, "null");
        return new PaymentSystem(
                new Inventories(List.of(withPromotion, withoutPromotion)),
                new Promotions(List.of(promotion)));
    }

    @Test
    @DisplayName("생성에 성공한다.")
    void 성공_생성() {
        assertThatCode(() -> createSystem(10, 10)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("프로모션 기간이 지난 경우 일반 구매로 처리한다.")
    void 성공_안내_프로모션만료() {
        paymentSystem = createSystem(10, 10);

        Response response = paymentSystem.canBuy(PRODUCT_NAME, new Quantity(3), store, EXPIRED_DATE);

        assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }

    @Test
    @DisplayName("프로모션이 없는 상품 구매한다.")
    void buyWithNoPromotion() {
        Inventories inventories = new Inventories(List.of(new Inventory(product, 10, "null")));
        paymentSystem = new PaymentSystem(inventories, new Promotions(Collections.emptyList()));

        Response response = paymentSystem.canBuy(PRODUCT_NAME, new Quantity(3), store, VALID_DATE);

        assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }

    @Test
    @DisplayName("적용가능한 프로모션의 재고가 없을 경우 일반재고에서 함께 구매한다.")
    void 성공_안내_일반재고() {
        paymentSystem = createSystem(1, 10);
        Response response = paymentSystem.canBuy(PRODUCT_NAME, new Quantity(3), store, VALID_DATE);

        assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }

    @Test
    @DisplayName("프로모션 기간임에도 최소 구매 수량을 만족하지 못하면 프로모션 재고만 줄인다.")
    void 성공_안내_프로모션기간임에도조건에해당X() {
        paymentSystem = createSystem(10, 10);
        Response response = paymentSystem.canBuy(PRODUCT_NAME, new Quantity(1), store, VALID_DATE);

        assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }

    @Test
    @DisplayName("프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제하게 됨을 안내한다.")
    void 성공_안내_프로모션재고부족() {
        paymentSystem = createSystem(7, 10);
        Response response = paymentSystem.canBuy(PRODUCT_NAME, new Quantity(10), store, VALID_DATE);

        assertThat(response.status()).isEqualTo(ResponseStatus.OUT_OF_STOCK);
        assertThat(response.bonusQuantity()).isEqualTo(new Quantity(2));
        assertThat(response.noPromotionQuantity()).isEqualTo(new Quantity(4));
    }

    @Test
    @DisplayName("프로모션의 보너스 수량을 안내한다.")
    void notifyAvailableBonus() {
        Response response = createSystem(10, 0)
                .canBuy(PRODUCT_NAME, new Quantity(2), store, VALID_DATE);

        assertThat(response.status()).isEqualTo(ResponseStatus.CAN_GET_BONUS);
        assertThat(response.bonusQuantity().getQuantity()).isEqualTo(new Quantity(1).getQuantity());
        assertThat(response.canGetMoreQuantity().getQuantity()).isEqualTo(new Quantity(1).getQuantity());
    }

    @Test
    @DisplayName("멤버십은 프로모션이 적용되지 않은 상품에 대해서만 적용된다.")
    void 성공_안내_멤버십대상상품() {
        Product juice = new Product("juice", PRODUCT_PRICE);
        Inventory juiceInventory = new Inventory(juice, 10, "null");
        PaymentSystem system = new PaymentSystem(
                new Inventories(List.of(juiceInventory)),
                new Promotions(Collections.emptyList()));

        Response response = system.canBuy("juice", new Quantity(9), store, VALID_DATE);

        assertThat(response.status()).isEqualTo(ResponseStatus.BUY_WITH_NO_PROMOTION);
    }
}

package store.domain.receipt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.order.OrderResult;
import store.domain.product.Product;
import store.domain.product.stock.ProductStock;
import store.domain.promotion.Promotion;
import store.domain.promotion.PromotionResult;

class ReceiptTest {

    private static final String COKE = "콜라";
    private static final String CIDER = "사이다";

    @Test
    @DisplayName("멤버십 적용된 상황에서 영수증을 생성한다.")
    void 멤버십_적용된_상황에서_영수증을_생성한다() {
        // Given
        List<OrderResult> orderResults = List.of(createCokeOrderResult(), createCiderOrderResult());

        // When
        Receipt receipt = Receipt.from(orderResults, true);

        // Then
        assertAll(
                () -> assertThat(receipt.getPurchaseResults()).hasSize(2),
                () -> assertThat(receipt.getGiftResults()).hasSize(2),
                () -> assertThat(receipt.getTotalPurchaseAmount()).isEqualTo(15000),
                () -> assertThat(receipt.getTotalGiftDiscountAmount()).isEqualTo(3000),
                () -> assertThat(receipt.getTotalMembershipDiscountAmount()).isEqualTo(2400),
                () -> assertThat(receipt.getTotalPayAmount()).isEqualTo(9600)
        );
    }

    @Test
    @DisplayName("멤버십이 적용되지 않은 상황에서 영수증을 생성한다.")
    void 멤버십이_적용되지_않은_상황에서_영수증을_생성한다() {
        // Given
        List<OrderResult> orderResults = List.of(createCokeOrderResult(), createCiderOrderResult());

        // When
        Receipt receipt = Receipt.from(orderResults, false);

        // Then
        assertAll(
                () -> assertThat(receipt.getPurchaseResults()).hasSize(2),
                () -> assertThat(receipt.getGiftResults()).hasSize(2),
                () -> assertThat(receipt.getTotalPurchaseAmount()).isEqualTo(15000),
                () -> assertThat(receipt.getTotalGiftDiscountAmount()).isEqualTo(3000),
                () -> assertThat(receipt.getTotalMembershipDiscountAmount()).isEqualTo(0),
                () -> assertThat(receipt.getTotalPayAmount()).isEqualTo(12000)
        );
    }

    private OrderResult createCiderOrderResult() {
        Promotion ciderPromotion = makePromotion(CIDER);
        ProductStock ciderProductStock = new ProductStock(makeProduct(CIDER, ciderPromotion));
        ciderProductStock.addPromotionQuantity(10);
        ciderProductStock.addRegularQuantity(10);
        PromotionResult ciderPromotionResult = PromotionResult.makePromotionPurchaseResult(5, 1, 0);
        return OrderResult.of(ciderProductStock, ciderPromotionResult, 1200);
    }

    private OrderResult createCokeOrderResult() {
        Promotion cokePromotion = makePromotion(COKE);
        ProductStock cokeProductStock = new ProductStock(makeProduct(COKE, cokePromotion));
        cokeProductStock.addPromotionQuantity(7);
        cokeProductStock.addRegularQuantity(10);
        PromotionResult cokePromotionResult = PromotionResult.makeMixedPurchaseResult(10, 2, 4, 0);
        return OrderResult.of(cokeProductStock, cokePromotionResult, 1200);
    }

    private Product makeProduct(final String productName, final Promotion promotion) {
        return new Product(productName, 1000, promotion);
    }

    private Promotion makePromotion(final String productName) {
        LocalDate startDate = makeLocalDate(2024, 12, 1);
        LocalDate endDate = makeLocalDate(2024, 12, 20);
        return new Promotion(productName, 2, 1, startDate, endDate);
    }

    private LocalDate makeLocalDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

}

package store.domain.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.product.Product;
import store.domain.product.stock.ProductStock;

class PromotionProcessorTest {

    @Test
    @DisplayName("프로모션 유효기간이 지날 경우 프로모션 적용을 하지 않는다.")
    void 프로모션_유효기간이_지날_경우_프로모션_적용을_하지_않는다() {
        // Given
        Promotion promotion = makePromotion();
        ProductStock productStock = new ProductStock(makeProduct(promotion));
        productStock.addRegularQuantity(10);
        PromotionProcessor promotionProcessor = new PromotionProcessor(productStock);
        LocalDate now = makeLocalDate(2024, 12, 31);

        // When
        PromotionResult result = promotionProcessor.process(3, now);

        // Then
        assertAll(
                () -> assertThat(result.purchaseType()).isEqualTo(PurchaseType.REGULAR_ONLY),
                () -> assertThat(result.regularPriceQuantity()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("프로모션 재고가 부족할 경우 일부 수량에 대해 정가 결제를 안내한다.")
    void 프로모션_재고가_부족할_경우_일부_수량에_대해_정가_결제를_안내한다() {
        // Given
        Promotion promotion = makePromotion();
        ProductStock productStock = new ProductStock(makeProduct(promotion));
        productStock.addPromotionQuantity(7);
        PromotionProcessor promotionProcessor = new PromotionProcessor(productStock);
        LocalDate now = makeLocalDate(2024, 12, 13);

        // When
        PromotionResult result = promotionProcessor.process(10, now);

        // Then
        assertAll(
                () -> assertThat(result.purchaseType()).isEqualTo(PurchaseType.MIXED),
                () -> assertThat(result.regularPriceQuantity()).isEqualTo(4),
                () -> assertThat(result.totalQuantity()).isEqualTo(10),
                () -> assertThat(result.additionalBenefitQuantity()).isEqualTo(0),
                () -> assertThat(result.giftQuantity()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("추가 혜택을 받을 수 있는 경우 안내한다.")
    void 추가_혜택을_받을_수_있는_경우_안내한다() {
        // Given
        Promotion promotion = makePromotion();
        ProductStock productStock = new ProductStock(makeProduct(promotion));
        productStock.addPromotionQuantity(7);
        PromotionProcessor promotionProcessor = new PromotionProcessor(productStock);
        LocalDate now = makeLocalDate(2024, 12, 13);

        // When
        PromotionResult result = promotionProcessor.process(5, now);

        // Then
        assertAll(
                () -> assertThat(result.purchaseType()).isEqualTo(PurchaseType.PROMOTIONAL_ONLY),
                () -> assertThat(result.totalQuantity()).isEqualTo(5),
                () -> assertThat(result.additionalBenefitQuantity()).isEqualTo(1),
                () -> assertThat(result.giftQuantity()).isEqualTo(1) // 추가 혜택 수량 뺀 값
        );
    }

    @Test
    @DisplayName("할인을 적용한다.")
    void 할인을_적용한다() {
        // Given
        Promotion promotion = makePromotion();
        ProductStock productStock = new ProductStock(makeProduct(promotion));
        productStock.addPromotionQuantity(7);
        PromotionProcessor promotionProcessor = new PromotionProcessor(productStock);
        LocalDate now = makeLocalDate(2024, 12, 13);

        // When
        PromotionResult result = promotionProcessor.process(4, now);

        // Then
        assertAll(
                () -> assertThat(result.purchaseType()).isEqualTo(PurchaseType.PROMOTIONAL_ONLY),
                () -> assertThat(result.totalQuantity()).isEqualTo(4),
                () -> assertThat(result.regularPriceQuantity()).isEqualTo(0),
                () -> assertThat(result.additionalBenefitQuantity()).isEqualTo(0),
                () -> assertThat(result.giftQuantity()).isEqualTo(1) // 추가 혜택 수량 뺀 값
        );
    }

    private Product makeProduct(final Promotion promotion) {
        return new Product("콜라", 1000, promotion);
    }

    private Promotion makePromotion() {
        LocalDate startDate = makeLocalDate(2024, 12, 1);
        LocalDate endDate = makeLocalDate(2024, 12, 20);
        return new Promotion("콜라", 2, 1, startDate, endDate);
    }

    private LocalDate makeLocalDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }
}

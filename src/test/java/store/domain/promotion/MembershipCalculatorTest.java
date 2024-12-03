package store.domain.promotion;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MembershipCalculatorTest {

    @Test
    @DisplayName("정가 결제에 대해 멤버십 비용을 계산한다.")
    void 정가_결제에_대해_멤버십_비용을_계산한다() {
        // Given
        MembershipCalculator membershipCalculator = new MembershipCalculator();
        PromotionResult result = PromotionResult.makeRegularPurchaseResult(4);

        // When & Then
        assertThat(membershipCalculator.calculate(1000, result)).isEqualTo(1200);
    }

    @Test
    @DisplayName("정가 결제와 프로모션 결제가 함께 수행된 결제에 대해 멤버십 비용을 계산한다.")
    void 정가_결제와_프로모션_결제가_함께_수행된_결제에_대해_멤버십_비용을_계산한다() {
        // Given
        MembershipCalculator membershipCalculator = new MembershipCalculator();
        PromotionResult result = PromotionResult.makeMixedPurchaseResult(1, 4, 0, 1);

        // When & Then
        assertThat(membershipCalculator.calculate(1000, result)).isEqualTo(300);
    }

    @Test
    @DisplayName("프로모션 적용된 결제에 대해 멤버십 비용을 계산한다.")
    void 프로모션_적용된_결제에_대해_멤버십_비용을_계산한다() {
        // Given
        MembershipCalculator membershipCalculator = new MembershipCalculator();
        PromotionResult result = PromotionResult.makePromotionPurchaseResult(3, 0, 1);

        // When & Then
        assertThat(membershipCalculator.calculate(1000, result)).isEqualTo(0);
    }

    @Test
    @DisplayName("멤버십 최대 비용은 8000원이다.")
    void 멤버십_최대_비용은_8000_원이다() {
        // Given
        MembershipCalculator membershipCalculator = new MembershipCalculator();
        PromotionResult result = PromotionResult.makeRegularPurchaseResult(7);

        // When & Then
        assertThat(membershipCalculator.calculate(4000, result)).isEqualTo(8000);
    }
}

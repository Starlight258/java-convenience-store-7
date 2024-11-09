package store.domain.promotion;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;

@DisplayName("프로모션 리스트 테스트")
class PromotionsTest {

    @Test
    @DisplayName("프로모션이 null이면 예외가 발생한다")
    void 실패_생성() {
        // Given

        // When & Then
        assertThatThrownBy(() -> new Promotions(null))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("프로모션 리스트는 null일 수 없습니다.");
    }

    @Test
    @DisplayName("프로모션 이름으로 프로모션을 조회한다")
    void 성공_조회() {
        // Given
        String promotionName = "탄산2+1";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Promotion cokePromotion = new Promotion("탄산2+1", 2, 1, startDate, endDate);
        Promotion mdPromotion = new Promotion("MD추천상품", 1, 1, startDate, endDate);
        Promotions promotions = new Promotions(List.of(cokePromotion, mdPromotion));

        // When
        Promotion promotion = promotions.find(promotionName).get();

        // & Then
        Assertions.assertThat(promotion).isEqualTo(cokePromotion);
    }
}

package store.domain.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.quantity.Quantity;

@DisplayName("프로모션 리스트 테스트")
class PromotionsTest {

    @Test
    @DisplayName("프로모션이 null이면 예외가 발생한다")
    void throwExceptionWhenNull() {
        assertCustomIllegalArgumentException(() -> new Promotions(null))
                .hasMessageContaining("인자 값은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("프로모션 이름으로 프로모션을 조회한다")
    void findByName() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Promotion cokePromotion = new Promotion("탄산2+1", new Quantity(2), new Quantity(1), date, date.plusYears(1));
        Promotions promotions = new Promotions(List.of(cokePromotion));

        assertThat(promotions.find("탄산2+1", date.plusMonths(2)).get()).isEqualTo(cokePromotion);
    }
}

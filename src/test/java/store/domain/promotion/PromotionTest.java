package store.domain.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.domain.quantity.Quantity;

@DisplayName("할인 정보 테스트")
class PromotionTest {
    @Test
    @DisplayName("할인 정보를 저장한다")
    void createPromotion() {
        assertThatCode(() -> new Promotion("탄산2+1", new Quantity(2), new Quantity(1),
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시작 날짜가 이후 날짜 이전이면 예외가 발생한다")
    void throwExceptionWhenInvalidDate() {
        assertCustomIllegalArgumentException(() -> new Promotion("탄산2+1", new Quantity(2), new Quantity(1),
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 2, 28)))
                .hasMessageContaining("시작 날짜는 이후 날짜 이전일 수 없습니다.");
    }

    @ParameterizedTest
    @CsvSource({"2023-12-20,false", "2025-01-01,false", "2024-01-01,true", "2024-12-31,true"})
    @DisplayName("현재 날짜가 프로모션 기간인지 확인한다")
    void checkPromotionPeriod(String date, boolean isPeriod) {
        Promotion promotion = new Promotion("탄산2+1", new Quantity(2), new Quantity(1),
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertThat(promotion.isPromotionPeriod(LocalDate.parse(date))).isEqualTo(isPeriod);
    }
}

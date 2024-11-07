package store;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("할인 정보 테스트")
class PromotionTest {

    @Test
    @DisplayName("할인 정보를 저장한다")
    void 성공_생성_할인정보() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // When & Then
        assertThatCode(() -> {
            new Promotion("탄산2+1", 2, 1, startDate, endDate);
        }).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("프로모션 이름이 비어있거나 null이면 예외가 발생한다")
    void 실패_생성_프로모션이름(String input) {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // When & Then
        assertThatThrownBy(() -> new Promotion(input, 2, 1, startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("프로모션 이름이 비어있거나 null일 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("구매 수량이 0 또는 음수이면 예외가 발생한다")
    void 실패_생성_구매수량(int quantity) {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // When & Then
        assertThatThrownBy(() -> new Promotion("탄산2+1", quantity, 1, startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("구매 수량이 0 또는 음수일 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("보너스 수량이 0 또는 음수이면 예외가 발생한다")
    void 실패_생성_보너스수량(int quantity) {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // When & Then
        assertThatThrownBy(() -> new Promotion("탄산2+1", 2, quantity, startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("구매 수량이 0 또는 음수일 수 없습니다.");
    }
}

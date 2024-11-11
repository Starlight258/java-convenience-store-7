package store.util;

import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("입력값 검증 테스트")
class InputValidatorTest {

    @Nested
    @DisplayName("null or blank 검증 테스트")
    class nullOrBlank_검증_테스트 {

        @Test
        @DisplayName("null이면 예외가 발생한다.")
        void 실패_검증_null() {
            assertCustomIllegalArgumentException(() -> InputValidator.validateNotNullOrBlank(null))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("인자 값은 null이거나 공백일 수 없습니다");
        }

        @Test
        @DisplayName("비어있으면 예외가 발생한다.")
        void 실패_검증_empty() {
            assertCustomIllegalArgumentException(() -> InputValidator.validateNotNullOrBlank(""))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("인자 값은 null이거나 공백일 수 없습니다");
        }

        @Test
        @DisplayName("공백이면 예외가 발생한다.")
        void 실패_검증_공백() {
            assertCustomIllegalArgumentException(() -> InputValidator.validateNotNullOrBlank(" "))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("인자 값은 null이거나 공백일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("null or empty 검증 테스트")
    class nullOrEmpty_검증_테스트 {

        @Test
        @DisplayName("null이면 예외가 발생한다.")
        void 실패_검증_null() {
            assertCustomIllegalArgumentException(() -> InputValidator.validateNotNullOrEmpty(null))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("인자 값은 null이거나 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("비어있으면 예외가 발생한다.")
        void 실패_검증_empty() {
            assertCustomIllegalArgumentException(
                    () -> InputValidator.validateNotNullOrEmpty(Collections.emptyList()))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("인자 값은 null이거나 비어있을 수 없습니다");
        }
    }
}

package store;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConverterTest {

    @Test
    @DisplayName("문자열을 받아 정수로 변환한다.")
    void 성공_정수변환(){
        // Given
        String input = "3";

        // When
        int number = Converter.convertToInteger(input);

        // Then
        Assertions.assertThat(number).isEqualTo(3);
    }
}

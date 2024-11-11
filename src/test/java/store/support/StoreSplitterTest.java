package store.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구분자 테스트")
class StoreSplitterTest {

    @Test
    @DisplayName("문자열을 구분자로 구분한다.")
    void 성공_문자열구분() {
        // Given
        StoreSplitter splitter = new StoreSplitter(",");
        String text = "mint,dobby";
        // When
        List<String> splittedText = splitter.split(text);
        // Then
        assertThat(splittedText).containsExactly("mint", "dobby");
    }
}

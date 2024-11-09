package store;

import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;
import static camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest;
import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.test.NsTest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ApplicationTest extends NsTest {
    @Test
    void 파일에_있는_상품_목록_출력() {
        assertSimpleTest(() -> {
            run("[물-1]", "N", "N");
            assertThat(output()).contains(
                    "- 콜라 1,000원 10개 탄산2+1",
                    "- 콜라 1,000원 10개",
                    "- 사이다 1,000원 8개 탄산2+1",
                    "- 사이다 1,000원 7개",
                    "- 오렌지주스 1,800원 9개 MD추천상품",
                    "- 오렌지주스 1,800원 재고 없음",
                    "- 탄산수 1,200원 5개 탄산2+1",
                    "- 탄산수 1,200원 재고 없음",
                    "- 물 500원 10개",
                    "- 비타민워터 1,500원 6개",
                    "- 감자칩 1,500원 5개 반짝할인",
                    "- 감자칩 1,500원 5개",
                    "- 초코바 1,200원 5개 MD추천상품",
                    "- 초코바 1,200원 5개",
                    "- 에너지바 2,000원 5개",
                    "- 정식도시락 6,400원 8개",
                    "- 컵라면 1,700원 1개 MD추천상품",
                    "- 컵라면 1,700원 10개"
            );
        });
    }

    @Test
    void 여러_개의_일반_상품_구매() {
        assertSimpleTest(() -> {
            run("[비타민워터-3],[물-2],[정식도시락-2]", "N", "N");
            assertThat(output().replaceAll("\\s", "")).contains("내실돈18,300");
        });
    }

    @Test
    void 기간에_해당하지_않는_프로모션_적용() {
        assertNowTest(() -> {
            run("[감자칩-2]", "N", "N");
            assertThat(output().replaceAll("\\s", "")).contains("내실돈3,000");
        }, LocalDate.of(2024, 2, 1).atStartOfDay());
    }

    @Test
    void 예외_테스트() {
        assertSimpleTest(() -> {
            runException("[컵라면-12]", "N", "N");
            assertThat(output()).contains("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        });
    }

    @Nested
    @DisplayName("재입력")
    class 재입력_테스트 {

        @Test
        void 구매할_상품과_수량형식_올바르지않음_재입력() {
            assertSimpleTest(() -> {
                run("물3,[정식도시락-2]", "[물-3],[정식도시락-2]");
                assertThat(output()).contains("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
                assertThat(output()).contains("멤버십 할인을 받으시겠습니까? (Y/N)");
            });
        }

        @Test
        void 존재하지않는상품입력_재입력() {
            assertSimpleTest(() -> {
                run("[비타민민-3],[물-2]", "[비타민워터-3],[물-2]");
                assertThat(output()).contains("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
                assertThat(output()).contains("멤버십 할인을 받으시겠습니까? (Y/N)");
            });
        }

        @Test
        void 재고초과_재입력() {
            assertSimpleTest(() -> {
                run("[비타민워터-32],[물-2]", "[비타민워터-2],[물-2]");
                assertThat(output()).contains("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
                assertThat(output()).contains("멤버십 할인을 받으시겠습니까? (Y/N)");
            });
        }

        @Test
        void 프로모션_수량보너스_YOrN_재입력() {
            assertSimpleTest(() -> {
                run("[콜라-2]", "YY", "Y");
                assertThat(output()).contains("현재 콜라은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
                assertThat(output()).contains("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
                assertThat(output()).contains("멤버십 할인을 받으시겠습니까? (Y/N)");
            });
        }

        @Test
        void 프로모션_재고YOrN_재입력() {
            assertSimpleTest(() -> {
                run("[콜라-15]", "YY", "Y");
                assertThat(output()).contains("현재 콜라 6개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
                assertThat(output()).contains("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
                assertThat(output()).contains("멤버십 할인을 받으시겠습니까? (Y/N)");
            });
        }

        @Test
        void 멤버십할인YOrN_재입력() {
            assertSimpleTest(() -> {
                run("[콜라-15]", "YY", "Y");
                assertThat(output()).contains("현재 콜라 6개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
                assertThat(output()).contains("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
                assertThat(output()).contains("멤버십 할인을 받으시겠습니까? (Y/N)");
            });
        }
    }

    @Override
    public void runMain() {
        Application.main(new String[]{});
    }
}

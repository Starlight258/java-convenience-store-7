package store.view;

import static store.exception.ErrorMessage.INVALID_ANSWER_FORMAT;

import java.util.Arrays;
import store.domain.quantity.Quantity;
import store.exception.CustomIllegalArgumentException;
import store.exception.ExceptionHandler;

public class InteractionView {

    public enum Answer {
        YES("Y"), NO("N");

        private final String value;

        Answer(final String value) {
            this.value = value;
        }

        public static Answer from(String input) {
            return Arrays.stream(values())
                    .filter(answer -> answer.value.equals(input))
                    .findFirst()
                    .orElseThrow(() -> new CustomIllegalArgumentException(INVALID_ANSWER_FORMAT.getMessage()));
        }

        public boolean isYes() {
            return this == YES;
        }
    }

    private final InputView inputView;
    private final OutputView outputView;
    private final ExceptionHandler exceptionHandler;

    public InteractionView(final InputView inputView, final OutputView outputView,
                           final ExceptionHandler exceptionHandler) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.exceptionHandler = exceptionHandler;
    }

    public boolean askForBonus(String productName, Quantity quantity) {
        outputView.showFreeQuantity(productName, quantity.getQuantity());
        return isYes();
    }

    public boolean askForNoPromotion(String productName, int quantity) {
        outputView.showPromotionDiscount(productName, quantity);
        return isYes();
    }

    public boolean isYes() {
        return exceptionHandler.retryOn(() ->
                Answer.from(inputView.readLine())).isYes();
    }
}

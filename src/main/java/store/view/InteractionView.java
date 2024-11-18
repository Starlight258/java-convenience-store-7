package store.view;

import static store.exception.ExceptionMessages.ONLY_YES_OR_NO;

import java.util.Arrays;
import store.domain.quantity.Quantity;
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
                    .orElseThrow(() -> new IllegalArgumentException(ONLY_YES_OR_NO.getMessageWithPrefix()));
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
        return readAnswer();
    }

    public boolean askForNoPromotion(String productName, int quantity) {
        outputView.showPromotionDiscount(productName, quantity);
        return readAnswer();
    }

    public boolean readAnswer() {
        return exceptionHandler.retryWithReturn(() ->
                        Answer.from(inputView.readLine()))
                .equals(Answer.YES);
    }
}

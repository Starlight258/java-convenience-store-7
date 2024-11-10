package store.view;

import static store.exception.ExceptionMessages.WRONG_INPUT;

import store.exception.ExceptionHandler;

public class InteractionView {

    private static final String YES = "Y";
    private static final String NO = "N";

    private final InputView inputView;
    private final OutputView outputView;
    private final ExceptionHandler exceptionHandler;

    public InteractionView(final InputView inputView, final OutputView outputView,
                           final ExceptionHandler exceptionHandler) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.exceptionHandler = exceptionHandler;
    }

    public boolean askForBonus(String productName, int quantity) {
        outputView.showFreeQuantity(productName, quantity);
        return readYOrN();
    }

    public boolean askForNoPromotion(String productName, int quantity) {
        outputView.showPromotionDiscount(productName, quantity);
        return readYOrN();
    }

    public boolean readYOrN() {
        return exceptionHandler.retryWithReturn(() -> {
            String answer = inputView.readLine();
            validateAnswer(answer);
            return answer.equals(YES);
        });
    }

    private void validateAnswer(String input) {
        if (input.equals(YES) || input.equals(NO)) {
            return;
        }
        throw new IllegalArgumentException(WRONG_INPUT.getErrorMessage());
    }
}

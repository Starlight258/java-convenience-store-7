package store.view;

import static store.exception.ExceptionMessages.WRONG_INPUT;

public class InteractionView {

    private static final String YES = "Y";
    private static final String NO = "N";

    private final InputView inputView;
    private final OutputView outputView;

    public InteractionView(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public boolean askForBonus(String productName, int quantity) {
        outputView.showFreeQuantity(productName, quantity);
        return readYOrN().equals(YES);
    }

    public boolean askForNoPromotion(String productName, int quantity){
        outputView.showPromotionDiscount(productName, quantity);
        return readYOrN().equals(YES);
    }

    private String readYOrN() {
        while (true) {
            try {
                String answer = inputView.readLine();
                validateAnswer(answer);
                return answer;
            } catch (IllegalArgumentException exception) {
                outputView.showExceptionMessage(exception.getMessage());
            }
        }
    }

    private void validateAnswer(String input) {
        if (input.equals(YES) || input.equals(NO)) {
            return;
        }
        throw new IllegalArgumentException(WRONG_INPUT.getErrorMessage());
    }
}

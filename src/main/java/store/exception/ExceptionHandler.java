package store.exception;

import java.util.function.Supplier;
import store.view.OutputView;

public class ExceptionHandler {

    private final OutputView outputView;

    public ExceptionHandler(final OutputView outputView) {
        this.outputView = outputView;
    }

    public <T> T retryWithReturn(Supplier<T> action) {
        while (true) {
            try {
                return action.get();
            } catch (IllegalArgumentException | IllegalStateException e) {
                outputView.showExceptionMessage(e.getMessage());
            }
        }
    }

    public void retryWithNoReturn(Runnable action) {
        while (true) {
            try {
                action.run();
                return;
            } catch (IllegalArgumentException | IllegalStateException e) {
                outputView.showExceptionMessage(e.getMessage());
            }
        }
    }

    public <T> T actionOfFileRead(Supplier<T> action) {
        try {
            return action.get();
        } catch (RuntimeException e) {
            outputView.showExceptionMessage(e.getMessage());
            throw e;
        }
    }
}

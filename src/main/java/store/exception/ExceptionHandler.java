package store.exception;

import java.util.function.Supplier;
import store.view.OutputView;

public class ExceptionHandler {

    private static final int MAX_RETRY = 3;

    private final OutputView outputView;

    public ExceptionHandler(final OutputView outputView) {
        this.outputView = outputView;
    }

    public <T> T retryOn(Supplier<T> action) {
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            try {
                return action.get();
            } catch (IllegalArgumentException e) {
                handleError(e, attempt);
            }
        }
        throw new IllegalArgumentException();
    }

    private void handleError(Exception e, int attempt) {
        if (attempt == MAX_RETRY - 1) {
            throw new IllegalArgumentException();
        }
        outputView.showExceptionMessage((e.getMessage()));
    }

    public boolean tryWithoutThrow(Supplier<Boolean> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException | IllegalStateException e) {
            outputView.showExceptionMessage(e.getMessage());
            return false;
        }
    }

    public <T> T tryWithThrow(Supplier<T> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException | IllegalStateException e) {
            outputView.showExceptionMessage(e.getMessage());
            throw e;
        }
    }

    public void tryVoid(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            outputView.showExceptionMessage(e.getMessage());
            throw e;
        }
    }
}

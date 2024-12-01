package store.util;

import java.util.function.Supplier;
import store.view.OutputView;

public class ExceptionHandler {

    private final OutputView outputView;

    public ExceptionHandler(final OutputView outputView) {
        this.outputView = outputView;
    }

    // 최대 시도 횟수만큼 재시도하며 결과를 반환
    public <T> T retryOn(Supplier<T> action) {
        while (true) {
            try {
                return action.get();
            } catch (IllegalArgumentException e) {
                outputView.showException(e);
            }
        }
    }

    public void retryOn(Runnable callback) {
        while (true) {
            try {
                callback.run();
                return;
            } catch (IllegalArgumentException e) {
                outputView.showException(e);
            }
        }
    }
}

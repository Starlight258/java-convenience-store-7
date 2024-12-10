package store;

import camp.nextstep.edu.missionutils.Console;
import store.controller.StoreController;
import store.service.StoreService;
import store.exception.ExceptionHandler;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        StoreController controller = createController();
        try {
            controller.process();
        } finally {
            Console.close();
        }
    }

    private static StoreController createController() {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        ExceptionHandler exceptionHandler = new ExceptionHandler(outputView);
        StoreService service = new StoreService();
        return new StoreController(inputView, outputView, exceptionHandler, service);
    }
}

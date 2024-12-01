package store;

import camp.nextstep.edu.missionutils.Console;
import store.controller.StoreController;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        StoreController controller = new StoreController(inputView, outputView);
        try {
            controller.process();
        } finally {
            Console.close();
        }
    }
}

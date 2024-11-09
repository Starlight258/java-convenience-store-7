package store;

import camp.nextstep.edu.missionutils.Console;
import store.controller.StoreController;
import store.support.StoreFormatter;
import store.view.InputView;
import store.view.OutputView;

public class Application {

    public static final int FORMAT_SIZE = 12;

    public static void main(String[] args) {
        InputView inputView = new InputView();
        StoreFormatter formatter = new StoreFormatter(FORMAT_SIZE);
        OutputView outputView = new OutputView(formatter);
        StoreController controller = new StoreController(inputView, outputView);
        controller.process();
        Console.close();
    }
}

package store;

import camp.nextstep.edu.missionutils.Console;
import store.controller.StoreController;
import store.support.StoreFormatter;
import store.support.StoreSplitter;
import store.view.InputView;
import store.view.InteractionView;
import store.view.OutputView;

public class Application {

    public static final int FORMAT_SIZE = 12;
    public static final String DELIMITER = ",";

    public static void main(String[] args) {
        InputView inputView = new InputView();
        StoreFormatter formatter = new StoreFormatter(FORMAT_SIZE);
        OutputView outputView = new OutputView(formatter);
        StoreSplitter splitter = new StoreSplitter(DELIMITER);
        InteractionView interactionView = new InteractionView(inputView, outputView);
        StoreController controller = new StoreController(inputView, outputView, splitter, formatter, interactionView);
        controller.process();
        Console.close();
    }
}

package store;

import camp.nextstep.edu.missionutils.Console;
import java.util.NoSuchElementException;
import store.controller.StoreController;
import store.exception.ExceptionHandler;
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
        ExceptionHandler exceptionHandler = new ExceptionHandler(outputView);
        InteractionView interactionView = new InteractionView(inputView, outputView, exceptionHandler);
        StoreController controller = new StoreController(inputView, outputView, splitter, formatter, interactionView,
                exceptionHandler);
        try {
            controller.process();
        } catch (NoSuchElementException ignored) {
        } finally {
            Console.close();
        }
    }
}

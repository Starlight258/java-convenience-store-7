package store.config;

import store.controller.StoreController;
import store.exception.ExceptionHandler;
import store.support.StoreFormatter;
import store.support.StoreSplitter;
import store.view.InputView;
import store.view.InteractionView;
import store.view.OutputView;

public class StoreConfig {

    private static final int FORMAT_SIZE = 12;
    private static final String DELIMITER = ",";

    public StoreController createController() {
        InputView inputView = new InputView();
        StoreFormatter formatter = new StoreFormatter(FORMAT_SIZE);
        OutputView outputView = new OutputView();
        StoreSplitter splitter = new StoreSplitter(DELIMITER);
        ExceptionHandler exceptionHandler = new ExceptionHandler(outputView);
        InteractionView interactionView = new InteractionView(inputView, outputView, exceptionHandler);
        return new StoreController(inputView, outputView, splitter, formatter, interactionView,
                exceptionHandler);
    }
}

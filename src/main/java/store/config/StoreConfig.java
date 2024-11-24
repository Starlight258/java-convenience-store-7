package store.config;

import store.controller.StoreController;
import store.exception.ExceptionHandler;
import store.service.StoreService;
import store.support.StoreFormatter;
import store.support.StoreSplitter;
import store.view.InputView;
import store.view.InteractionView;
import store.view.OutputView;
import store.view.StoreFileReader;

public class StoreConfig {

    private static final String DELIMITER = ",";

    public StoreController createController() {
        InputView inputView = new InputView();
        StoreFormatter formatter = new StoreFormatter();
        OutputView outputView = new OutputView(formatter);
        ExceptionHandler exceptionHandler = new ExceptionHandler(outputView);
        InteractionView interactionView = new InteractionView(inputView, outputView, exceptionHandler);
        StoreService storeService = createService(interactionView);
        return new StoreController(inputView, outputView, interactionView,
                exceptionHandler, storeService, formatter);
    }

    private StoreService createService(final InteractionView interactionView) {
        StoreSplitter splitter = new StoreSplitter(DELIMITER);
        StoreFileReader fileReader = new StoreFileReader();
        return new StoreService(splitter, fileReader, interactionView);
    }
}

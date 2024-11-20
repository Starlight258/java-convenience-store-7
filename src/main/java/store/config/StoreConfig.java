package store.config;

import store.controller.StoreController;
import store.exception.ExceptionHandler;
import store.service.StoreService;
import store.view.InputView;
import store.view.InteractionView;
import store.view.OutputView;
import store.view.StoreView;

public class StoreConfig {

    public StoreController createController() {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        ExceptionHandler exceptionHandler = new ExceptionHandler(outputView);
        InteractionView interactionView = new InteractionView(inputView, outputView, exceptionHandler);
        StoreView storeView = new StoreView(inputView, outputView, interactionView);
        StoreService storeService = createService(interactionView);
        return new StoreController(storeView, exceptionHandler, storeService);
    }

    private StoreService createService(final InteractionView interactionView) {
        return new StoreService(interactionView);
    }
}

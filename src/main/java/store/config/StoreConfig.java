package store.config;

import store.controller.StoreController;
import store.domain.factory.StoreFactory;
import store.exception.ExceptionHandler;
import store.service.StoreService;
import store.util.StoreDataLoader;
import store.util.StoreInitializer;
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
        StoreInitializer storeInitializer = createStoreInitializer();
        return new StoreController(storeView, exceptionHandler, storeService, storeInitializer);
    }

    private StoreService createService(final InteractionView interactionView) {
        return new StoreService(interactionView);
    }

    private StoreInitializer createStoreInitializer() {
        StoreDataLoader storeDataLoader = new StoreDataLoader();
        StoreFactory storeFactory = new StoreFactory();
        return new StoreInitializer(storeDataLoader, storeFactory);
    }
}

package store;

import store.config.StoreConfig;
import store.controller.StoreController;

public class Application {

    public static void main(String[] args) {
        StoreConfig config = new StoreConfig();
        StoreController controller = config.createController();
        controller.process();
    }
}

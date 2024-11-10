package store;

import camp.nextstep.edu.missionutils.Console;
import store.config.StoreConfig;
import store.controller.StoreController;

public class Application {

    public static void main(String[] args) {
        StoreConfig config = new StoreConfig();
        StoreController controller = config.createController();
        try {
            controller.process();
        } finally {
            Console.close();
        }
    }
}

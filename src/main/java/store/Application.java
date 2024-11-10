package store;

import camp.nextstep.edu.missionutils.Console;
import java.util.NoSuchElementException;
import store.config.StoreConfig;
import store.controller.StoreController;

public class Application {

    public static void main(String[] args) {
        StoreConfig config = new StoreConfig();
        StoreController controller = config.createController();
        try {
            controller.process();
        } catch (NoSuchElementException ignored) {
        } finally {
            Console.close();
        }
    }
}

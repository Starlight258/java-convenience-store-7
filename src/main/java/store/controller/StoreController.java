package store.controller;

import static store.exception.ErrorMessage.INVALID_FILE_FORMAT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.promotion.Promotion;
import store.util.FileContentParser;
import store.util.StoreFileReader;
import store.util.StringParser;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private final InputView inputView;
    private final OutputView outputView;

    public StoreController(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void process() {
        outputView.showWelcome();
        // 프로모션 -> 인벤토리
        List<Promotion> promotions = makePromotions();
        Inventory inventory = makeInventory(promotions);
        outputView.showInventory(inventory);
    }

    private List<Promotion> makePromotions() {
        List<String> inputs = FileContentParser.removeHeaders(StoreFileReader.readPromotions());
        List<Promotion> promotions = new ArrayList<>();
        for (String input : inputs) {
            List<String> tokens = StringParser.parseByDelimiter(input, ",");
            int buyQuantity = StringParser.parseToInteger(tokens.get(1), INVALID_FILE_FORMAT);
            int getQuantity = StringParser.parseToInteger(tokens.get(2), INVALID_FILE_FORMAT);
            LocalDate startDate = LocalDate.parse(tokens.get(3));
            LocalDate endDate = LocalDate.parse(tokens.get(4));
            promotions.add(new Promotion(tokens.get(0), buyQuantity, getQuantity, startDate, endDate));
        }
        return promotions;
    }

    //        name,price,quantity,promotion

    private Inventory makeInventory(final List<Promotion> promotions) {
        List<String> inputs = FileContentParser.removeHeaders(StoreFileReader.readInventories());
        List<Product> products = new ArrayList<>();
        Inventory inventory = new Inventory();
        for (String input : inputs) {
            List<String> tokens = StringParser.parseByDelimiter(input, ",");
            Product product = createProduct(products, tokens);
            int quantity = StringParser.parseToInteger(tokens.get(2), INVALID_FILE_FORMAT);
            Promotion promotion = findPromotion(promotions, tokens.get(3));
            inventory.add(product, quantity, promotion);
        }
        return inventory;
    }

    private Product createProduct(final List<Product> products, final List<String> tokens) {
        String name = tokens.get(0);
        int price = StringParser.parseToInteger(tokens.get(1), INVALID_FILE_FORMAT);

        return products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseGet(() -> {
                    Product newProduct = new Product(name, price);
                    products.add(newProduct);
                    return newProduct;
                });
    }

    private Promotion findPromotion(final List<Promotion> promotions, final String name) {
        return promotions.stream()
                .filter(promotion2 -> promotion2.hasName(name))
                .findFirst()
                .orElse(null);
    }
}

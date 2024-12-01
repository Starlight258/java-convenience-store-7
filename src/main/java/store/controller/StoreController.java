package store.controller;

import static store.exception.ErrorMessage.INVALID_FILE_FORMAT;
import static store.exception.ErrorMessage.INVALID_ORDER_FORMAT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import store.domain.OrderProcessor;
import store.domain.order.Order;
import store.domain.order.Orders;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.promotion.Promotion;
import store.util.ExceptionHandler;
import store.util.FileContentParser;
import store.util.InputValidator;
import store.util.StoreFileReader;
import store.util.StringParser;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private static final String ORDER_REGEX = "^\\[([가-힣a-zA-Z]+)-([1-9]\\d*)\\]$";
    private static final Pattern PATTERN = Pattern.compile(ORDER_REGEX);

    private final InputView inputView;
    private final OutputView outputView;
    private final ExceptionHandler exceptionHandler;

    public StoreController(final InputView inputView, final OutputView outputView,
                           final ExceptionHandler exceptionHandler) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.exceptionHandler = exceptionHandler;
    }

    public void process() {
        outputView.showWelcome();
        // 프로모션 -> 인벤토리
        List<Promotion> promotions = makePromotions();
        Inventory inventory = makeInventory(promotions);
        outputView.showInventory(inventory);
        OrderProcessor orderProcessor = new OrderProcessor(inventory);
        order(orderProcessor);
    }

    private void order(final OrderProcessor orderProcessor) {
        outputView.requestOrder();
        Orders orders = createOrders();
        for (Order order : orders.getOrders()) {
            orderProcessor.process(order);
        }
    }

    private Orders createOrders() {
        List<Order> orders = new ArrayList<>();
        return exceptionHandler.retryOn(() -> {
            List<String> inputs = inputView.readOrder();
            for (String input : inputs) {
                Order order = createOrder(input);
                orders.add(order);
            }
            return new Orders(orders);
        });
    }

    private Order createOrder(final String input) {
        InputValidator.isInvalidPattern(input, PATTERN, INVALID_ORDER_FORMAT);
        List<String> groups = StringParser.findMatchingGroups(input, PATTERN);
        String productName = groups.get(0);
        int quantity = StringParser.parseToInteger(groups.get(1), INVALID_ORDER_FORMAT);
        return new Order(productName, quantity);
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

package store.controller;

import static store.exception.ErrorMessage.INVALID_FILE_FORMAT;
import static store.exception.ErrorMessage.INVALID_ORDER_FORMAT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import store.domain.command.Answer;
import store.domain.order.Order;
import store.domain.order.Orders;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.promotion.Promotion;
import store.domain.promotion.PromotionResult;
import store.service.StoreService;
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
    private final StoreService storeService;

    public StoreController(final InputView inputView, final OutputView outputView,
                           final ExceptionHandler exceptionHandler,
                           final StoreService storeService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.exceptionHandler = exceptionHandler;
        this.storeService = storeService;
    }

    public void process() {
        outputView.showWelcome();
        // 프로모션 -> 인벤토리
        List<Promotion> promotions = makePromotions();
        Inventory inventory = makeInventory(promotions);
        outputView.showInventory(inventory);
        order(inventory);
    }

    private void order(final Inventory inventory) {
        outputView.requestOrder();
        Orders orders = createOrders();
        for (Order order : orders.getOrders()) {
            PromotionResult promotionResult = storeService.processOrder(inventory, order);
            promotionResult = processPaymentOption(inventory, order, promotionResult);
            promotionResult = processBenefitOption(inventory, order, promotionResult);
        }
    }

    private PromotionResult processPaymentOption(final Inventory inventory, final Order order,
                                      final PromotionResult promotionResult) {
        if (!promotionResult.askRegularPayment()) {
            return promotionResult;
        }
        outputView.requestRegularPayment(order.getName(), promotionResult.regularPriceQuantity());
        boolean wantRegularPayment = Answer.from(inputView.readRegularPayment()).isYes();
        if (wantRegularPayment) {
            return storeService.processRegularPayment(inventory, order, promotionResult);
        }
        return storeService.processOnlyPromotionPayment(inventory, order, promotionResult);
    }

    private PromotionResult processBenefitOption(final Inventory inventory, final Order order,
                                                 final PromotionResult promotionResult) {
        if (!promotionResult.askBenefit()) {
            return promotionResult;
        }
        outputView.requestBenefit(order.getName());
        boolean wantBenefit = Answer.from(inputView.readBenefitAnswer()).isYes();
        if (wantBenefit) {
            return storeService.processBenefitOption(inventory, order, promotionResult);
        }
        return storeService.processNoBenefitOption(inventory, order, promotionResult);
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
        int quantity = StringParser.parseToInteger(groups.get(1), INVALID_ORDER_FORMAT);
        return new Order(groups.get(0), quantity);
    }

    private List<Promotion> makePromotions() {
        List<String> inputs = FileContentParser.removeHeaders(StoreFileReader.readPromotions());
        List<Promotion> promotions = new ArrayList<>();
        for (String input : inputs) {
            List<String> tokens = StringParser.parseByDelimiter(input, ",");
            String name = tokens.get(0);
            int buyQuantity = StringParser.parseToInteger(tokens.get(1), INVALID_FILE_FORMAT);
            int getQuantity = StringParser.parseToInteger(tokens.get(2), INVALID_FILE_FORMAT);
            LocalDate startDate = LocalDate.parse(tokens.get(3));
            LocalDate endDate = LocalDate.parse(tokens.get(4));
            promotions.add(new Promotion(name, buyQuantity, getQuantity, startDate, endDate));
        }
        return promotions;
    }

    private Inventory makeInventory(final List<Promotion> promotions) {
        List<String> inputs = FileContentParser.removeHeaders(StoreFileReader.readInventories());
        Inventory inventory = new Inventory();
        for (String input : inputs) {
            List<String> tokens = StringParser.parseByDelimiter(input, ",");
            String productName = tokens.get(0);
            int price = StringParser.parseToInteger(tokens.get(1), INVALID_FILE_FORMAT);
            int quantity = StringParser.parseToInteger(tokens.get(2), INVALID_FILE_FORMAT);
            Promotion promotion = findPromotion(promotions, tokens.get(3));
            Product product = new Product(productName, price, promotion);
            inventory.addProductStock(product, quantity);
        }
        return inventory;
    }

    private Promotion findPromotion(final List<Promotion> promotions, final String name) {
        return promotions.stream()
                .filter(promotion2 -> promotion2.hasName(name))
                .findFirst()
                .orElse(null);
    }
}

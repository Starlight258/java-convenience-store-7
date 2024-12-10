package store.controller;

import static store.exception.ErrorMessage.INVALID_FILE_FORMAT;
import static store.exception.ErrorMessage.INVALID_ORDER_FORMAT;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import store.domain.command.Answer;
import store.domain.dto.InventoryDto;
import store.domain.order.Order;
import store.domain.order.OrderResult;
import store.domain.order.Orders;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.product.stock.ProductStock;
import store.domain.promotion.Promotion;
import store.domain.promotion.PromotionResult;
import store.domain.receipt.Receipt;
import store.exception.ExceptionHandler;
import store.service.StoreService;
import store.util.FileContentParser;
import store.util.InputValidator;
import store.util.StoreFileReader;
import store.util.StringParser;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private static final String ORDER_REGEX = "^\\[([가-힣a-zA-Z]+)-([1-9]\\d*)\\]$";
    private static final Pattern PATTERN = Pattern.compile(ORDER_REGEX);
    private static final String DELIMITER = ",";

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
        List<Promotion> promotions = makePromotions();
        Inventory inventory = makeInventory(promotions);
        processOrderLoop(inventory);
    }

    private void processOrderLoop(final Inventory inventory) {
        do {
            processOrderSession(inventory);
        } while (shouldContinue());
    }

    private void processOrderSession(final Inventory inventory) {
        outputView.showWelcome();
        showInventory(inventory);
        outputView.requestOrder();
        exceptionHandler.retryOn(() -> {
            Orders orders = createOrders(inventory);
            Receipt receipt = makeReceipt(inventory, orders);
            outputView.showReceipt(receipt);
        });
    }

    private void showInventory(final Inventory inventory) {
        List<InventoryDto> dtos = storeService.processInventory(inventory);
        outputView.showInventory(dtos);
    }

    private boolean shouldContinue() {
        outputView.requestRetry();
        Answer answer = exceptionHandler.retryOn(() -> Answer.from(inputView.readRetryAnswer()));
        outputView.showLine();
        return answer.isYes();
    }

    private Receipt makeReceipt(final Inventory inventory, final Orders orders) {
        return Receipt.from(processOrders(inventory, orders), requestMembership());
    }

    private List<OrderResult> processOrders(final Inventory inventory, final Orders orders) {
        return orders.getOrders().stream()
                .map(order -> processOrderSession(inventory, order))
                .toList();
    }

    private OrderResult processOrderSession(final Inventory inventory, final Order order) {
        ProductStock productStock = inventory.getProductStock(order.getName());
        PromotionResult promotionResult = storeService.processOrder(order.getQuantity(), productStock);
        promotionResult = processPaymentOption(productStock, promotionResult);
        promotionResult = processBenefitOption(productStock, promotionResult);
        int membershipDiscount = processMembership(productStock, promotionResult);
        return OrderResult.of(productStock, promotionResult, membershipDiscount);
    }

    private int processMembership(final ProductStock productStock, final PromotionResult promotionResult) {
        return storeService.processMembership(productStock.getProductPrice(), promotionResult);
    }

    private boolean requestMembership() {
        outputView.requestMembership();
        return exceptionHandler.retryOn(() -> Answer.from(inputView.readMembershipAnswer()).isYes());
    }

    private PromotionResult processPaymentOption(final ProductStock productStock,
                                                 final PromotionResult promotionResult) {
        if (!promotionResult.askRegularPayment()) {
            return promotionResult;
        }
        if (wantRegularPayment(productStock, promotionResult)) {
            return storeService.processRegularPayment(productStock, promotionResult);
        }
        return storeService.processOnlyPromotionPayment(productStock, promotionResult);
    }

    private boolean wantRegularPayment(final ProductStock productStock, final PromotionResult promotionResult) {
        outputView.requestRegularPayment(productStock.getProductName(), promotionResult.regularPriceQuantity());
        return exceptionHandler.retryOn(() -> Answer.from(inputView.readRegularPayment()).isYes());
    }

    private PromotionResult processBenefitOption(final ProductStock productStock,
                                                 final PromotionResult promotionResult) {
        if (!promotionResult.askBenefit()) {
            return promotionResult;
        }
        if (wantBenefit(productStock)) {
            return storeService.processBenefitOption(productStock, promotionResult);
        }
        return storeService.processNoBenefitOption(productStock, promotionResult);
    }

    private boolean wantBenefit(final ProductStock productStock) {
        outputView.requestBenefit(productStock.getProductName());
        return exceptionHandler.retryOn(() -> Answer.from(inputView.readBenefitAnswer()).isYes());
    }

    private Orders createOrders(final Inventory inventory) {
        return exceptionHandler.retryOn(() -> {
            List<String> inputs = inputView.readOrder();
            return new Orders(inputs.stream()
                    .map(input -> createOrder(input, inventory))
                    .toList());
        });
    }

    private Order createOrder(final String input, final Inventory inventory) {
        InputValidator.isInvalidPattern(input, PATTERN, INVALID_ORDER_FORMAT);
        List<String> groups = StringParser.findMatchingGroups(input, PATTERN);
        int quantity = StringParser.parseToInteger(groups.get(1), INVALID_ORDER_FORMAT);
        Order order = new Order(groups.get(0), quantity);
        inventory.validateProduct(order.getName());
        return order;
    }

    private List<Promotion> makePromotions() {
        List<String> inputs = FileContentParser.removeHeaders(StoreFileReader.readPromotions());
        return inputs.stream()
                .map(this::makePromotion)
                .toList();
    }

    private Promotion makePromotion(final String input) {
        List<String> tokens = StringParser.parseByDelimiter(input, DELIMITER);
        String name = tokens.get(0);
        int buyQuantity = StringParser.parseToInteger(tokens.get(1), INVALID_FILE_FORMAT);
        int getQuantity = StringParser.parseToInteger(tokens.get(2), INVALID_FILE_FORMAT);
        LocalDate startDate = LocalDate.parse(tokens.get(3));
        LocalDate endDate = LocalDate.parse(tokens.get(4));
        return new Promotion(name, buyQuantity, getQuantity, startDate, endDate);
    }

    private Inventory makeInventory(final List<Promotion> promotions) {
        List<String> inputs = FileContentParser.removeHeaders(StoreFileReader.readInventories());
        Inventory inventory = new Inventory();
        for (String input : inputs) {
            addStock(promotions, inventory, input);
        }
        return inventory;
    }

    private void addStock(final List<Promotion> promotions, final Inventory inventory, final String input) {
        List<String> tokens = StringParser.parseByDelimiter(input, DELIMITER);
        String productName = tokens.get(0);
        int price = StringParser.parseToInteger(tokens.get(1), INVALID_FILE_FORMAT);
        int quantity = StringParser.parseToInteger(tokens.get(2), INVALID_FILE_FORMAT);
        Promotion promotion = findPromotion(promotions, tokens.get(3));
        Product product = new Product(productName, price, promotion);
        inventory.addProductStock(product, quantity);
    }

    private Promotion findPromotion(final List<Promotion> promotions, final String name) {
        return promotions.stream()
                .filter(promotion2 -> promotion2.hasName(name))
                .findFirst()
                .orElse(null);
    }
}

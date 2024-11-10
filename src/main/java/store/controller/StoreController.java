package store.controller;

import static store.exception.ExceptionMessages.INVALID_FILE_FORMAT;
import static store.exception.ExceptionMessages.INVALID_FORMAT;
import static store.exception.ExceptionMessages.WRONG_INPUT;

import camp.nextstep.edu.missionutils.DateTimes;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.player.Orders;
import store.domain.price.Price;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.domain.system.PaymentSystem;
import store.exception.ExceptionHandler;
import store.response.Response;
import store.response.ResponseHandler;
import store.support.StoreFormatter;
import store.support.StoreSplitter;
import store.util.Converter;
import store.util.Parser;
import store.view.InputView;
import store.view.InteractionView;
import store.view.OutputView;

public class StoreController {

    public static final String INVENTORY_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/products.md";
    public static final String PROMOTION_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/promotions.md";
    public static final String REGEX = "^\\[((\\w*\\W*)-(\\d+))\\]$";
    public static final Pattern PATTERN = Pattern.compile(REGEX);

    private final InputView inputView;
    private final OutputView outputView;
    private final StoreSplitter splitter;
    private final StoreFormatter formatter;
    private final InteractionView interactionView;
    private final ExceptionHandler exceptionHandler;

    public StoreController(final InputView inputView, final OutputView outputView, final StoreSplitter splitter,
                           final StoreFormatter formatter,
                           final InteractionView interactionView, final ExceptionHandler exceptionHandler) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.splitter = splitter;
        this.formatter = formatter;
        this.interactionView = interactionView;
        this.exceptionHandler = exceptionHandler;
    }

    public void process() {
        PaymentSystem paymentSystem = initializePaymentSystem();
        processTransactions(paymentSystem);
    }

    private void processTransactions(final PaymentSystem paymentSystem) {
        exceptionHandler.retryWithNoReturn(() -> {
            if (processTransaction(paymentSystem)) {
                return;
            }
            outputView.showBlankLine();
        });
    }

    private PaymentSystem initializePaymentSystem() {
        return exceptionHandler.actionOfFileRead(() -> new PaymentSystem(makeInventories(), makePromotions()));
    }

    private boolean processTransaction(final PaymentSystem paymentSystem) {
        Inventories inventories = paymentSystem.getInventories();
        showWelcomeMessage(inventories);
        Orders orders = getPurchasedItems(inventories);
        convenienceStore(orders, paymentSystem);
        return !continueTransaction();
    }

    private boolean continueTransaction() {
        outputView.showAdditionalPurchase();
        return interactionView.readYOrN();
    }

    private void showWelcomeMessage(final Inventories inventories) {
        outputView.showStartMessage();
        showInventories(inventories);
    }

    private Promotions makePromotions() {
        List<String> promotionsFromSource = readPromotionFromSource();
        return addPromotion(promotionsFromSource);
    }

    private Inventories makeInventories() {
        List<String> inventoriesFromSource = readInventoryFromSource();
        return addInventory(inventoriesFromSource);
    }

    private List<String> readPromotionFromSource() {
        try {
            return inputView.readFile(PROMOTION_FILENAME);
        } catch (IOException exception) {
            throw new IllegalStateException(INVALID_FILE_FORMAT.getErrorMessage());
        }
    }

    private List<String> readInventoryFromSource() {
        try {
            return inputView.readFile(INVENTORY_FILENAME);
        } catch (IOException exception) {
            throw new IllegalStateException(INVALID_FILE_FORMAT.getErrorMessage());
        }
    }


    private void showInventories(final Inventories inventories) {
        for (Inventory inventory : inventories.getInventories()) {
            String message = formatter.makeInventoryMessage(inventory.getQuantity().getQuantity(),
                    inventory.getPromotionName(),
                    inventory.getProductName(), inventory.getProductPrice().getPrice());
            outputView.showMessage(message);
        }
    }

    private void convenienceStore(final Orders orders, final PaymentSystem paymentSystem) {
        Store store = initializeStore();
        purchase(orders, paymentSystem, store);
        Price membershipPrice = checkMemberShip(store.getMembership());
        showResultPrice(store.getReceipt(), membershipPrice);
    }

    private void purchase(final Orders orders, final PaymentSystem paymentSystem, final Store store) {
        Map<String, Quantity> purchasedItems = orders.getProductsToBuy();
        for (Entry<String, Quantity> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            Quantity quantity = entry.getValue();
            purchaseEachProduct(orders, paymentSystem, store, productName, quantity);
        }
    }

    private void purchaseEachProduct(final Orders orders, final PaymentSystem paymentSystem, final Store store,
                                     final String productName, final Quantity quantity) {
        LocalDate now = DateTimes.now().toLocalDate();
        Response response = paymentSystem.canBuy(productName, quantity, store, now);
        checkResponse(orders, store, productName, quantity, response);
    }

    private Store initializeStore() {
        return new Store(new Receipt(new LinkedHashMap<>(), new LinkedHashMap<>()),
                new Membership(new LinkedHashMap<>()));
    }

    private Orders getPurchasedItems(final Inventories inventories) {
        outputView.showCommentOfPurchase();
        return exceptionHandler.retryWithReturn(() -> {
            Orders purchasedItems = promptProductNameAndQuantity();
            inventories.getPurchasedItems(purchasedItems);
            return purchasedItems;
        });
    }

    private void showResultPrice(final Receipt receipt, final Price membershipPrice) {
        showResult(receipt, membershipPrice);
    }

    private Price checkMemberShip(final Membership membership) {
        outputView.showCommentOfMemberShip();
        if (!interactionView.readYOrN()) {
            return Price.zero();
        }
        return membership.calculateDiscount();
    }

    private void showResult(final Receipt receipt, final Price membershipPrice) {
        showPurchasedProducts(receipt);
        showBonus(receipt);
        showReceipt(receipt, membershipPrice);
    }

    private void showPurchasedProducts(final Receipt receipt) {
        outputView.showCommentOfInventory();
        for (Entry<Product, Quantity> entry : receipt.getPurchasedProducts().entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            Quantity quantity = entry.getValue();
            Price totalPrice = entry.getKey().getPrice().multiply(BigDecimal.valueOf(quantity.getQuantity()));
            outputView.showInventory(name, quantity.getQuantity(), totalPrice.getPrice());
        }
    }

    private void showReceipt(final Receipt receipt, final Price membershipPrice) {
        outputView.showReceiptStartMark();
        Entry<Quantity, Price> totalPurchases = receipt.getTotalPurchase();
        Price priceToPay = receipt.getPriceToPay(totalPurchases.getValue(), membershipPrice);
        Price totalPurchasePrice = totalPurchases.getValue();
        outputView.showTotalPrice(totalPurchases.getKey().getQuantity(), totalPurchasePrice.getPrice());
        outputView.showPromotionDiscountPrice(receipt.getPromotionDiscountPrice().getPrice());
        outputView.showMemberShipDiscountPrice(membershipPrice.getPrice());
        outputView.showMoneyToPay(priceToPay.getPrice());
    }

    private void showBonus(final Receipt receipt) {
        outputView.showBonus();
        for (Entry<Product, Quantity> entry : receipt.getBonusProducts().entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue().getQuantity();
            outputView.showBonusProduct(name, quantity);
        }
    }

    private void checkResponse(final Orders orders, final Store store, final String productName,
                               final Quantity quantity, final Response response) {
        ResponseHandler handler = new ResponseHandler(orders, store, productName, quantity, interactionView);
        handler.handle(response);
    }

    public Orders promptProductNameAndQuantity() {
        Orders purchasedItems = new Orders(new LinkedHashMap<>());
        String input = inputView.readLine();
        List<String> splitText = splitter.split(input);
        return addPurchasedItems(purchasedItems, splitText);
    }

    private Orders addPurchasedItems(final Orders orders,
                                     final List<String> splittedText) {
        for (String text : splittedText) {
            Matcher matcher = PATTERN.matcher(text);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(INVALID_FORMAT.getErrorMessage());
            }
            String productValue = matcher.group(2);
            int quantityValue = Converter.convertToInteger((matcher.group(3)));
            if (quantityValue == 0) {
                throw new IllegalArgumentException(WRONG_INPUT.getErrorMessage());
            }
            orders.put(productValue, new Quantity(quantityValue));
        }
        return orders;
    }

    private Promotions addPromotion(List<String> promotionsFromSource) {
        List<Promotion> promotions = new ArrayList<>();
        for (String input : promotionsFromSource) {
            if (input.startsWith("name")) {
                continue;
            }
            List<String> splittedText = splitter.split(input);
            Promotion promotion = getPromotion(splittedText);
            promotions.add(promotion);
        }
        return new Promotions(promotions);
    }

    private Promotion getPromotion(final List<String> splittedText) {
        Quantity purchaseQuantity = new Quantity(Converter.convertToInteger(splittedText.get(1)));
        Quantity bonusQuantity = new Quantity(Converter.convertToInteger(splittedText.get(2)));
        LocalDate startDate = Parser.parseToLocalDate(splittedText.get(3));
        LocalDate endDate = Parser.parseToLocalDate(splittedText.get(4));
        return new Promotion(splittedText.get(0), purchaseQuantity, bonusQuantity, startDate, endDate);
    }

    private Inventories addInventory(List<String> inputs) {
        List<Inventory> inventories = new ArrayList<>();
        for (String input : inputs) {
            if (input.startsWith("name")) {
                continue;
            }
            List<String> splittedText = splitter.split(input);
            Product product = new Product(splittedText.get(0), new BigDecimal(splittedText.get(1)));
            Inventory inventory = new Inventory(product, Converter.convertToInteger(splittedText.get(2)),
                    splittedText.get(3));
            inventories.add(inventory);
        }
        return new Inventories(inventories);
    }
}

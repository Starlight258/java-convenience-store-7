package store.service;

import static store.exception.ExceptionMessages.INVALID_FORMAT;
import static store.exception.ExceptionMessages.WRONG_INPUT;

import camp.nextstep.edu.missionutils.DateTimes;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.domain.system.PaymentSystem;
import store.response.Response;
import store.response.ResponseHandler;
import store.support.StoreSplitter;
import store.util.Converter;
import store.util.Parser;
import store.view.InteractionView;
import store.view.StoreFileReader;

public class StoreService {

    private static final String INVENTORY_FILENAME = "src/main/resources/products.md";
    private static final String PROMOTION_FILENAME = "src/main/resources/promotions.md";
    private static final String REGEX = "^\\[((\\w*\\W*)-(\\d+))\\]$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    public static final String NAME = "name";
    public static final String NULL = "null";

    private final StoreSplitter splitter;
    private final StoreFileReader fileReader;
    private final InteractionView interactionView;

    public StoreService(final StoreSplitter splitter, final StoreFileReader fileReader,
                        final InteractionView interactionView) {
        this.splitter = splitter;
        this.fileReader = fileReader;
        this.interactionView = interactionView;
    }

    public PaymentSystem initializePaymentSystem() {
        return new PaymentSystem(makeInventories(), makePromotions());
    }

    public Store initializeStore() {
        return new Store(new Receipt(new LinkedHashMap<>(), new LinkedHashMap<>()),
                new Membership(new LinkedHashMap<>()));
    }

    public Map<String, Quantity> createOrders(String input, Inventories inventories) {
        List<String> splitText = splitter.split(input);
        Map<String, Quantity> orders = parseOrders(splitText);
        inventories.getPurchasedItems(orders);
        return orders;
    }

    public void processPurchase(Map<String, Quantity> orders, PaymentSystem paymentSystem, Store store) {
        for (Entry<String, Quantity> entry : orders.entrySet()) {
            processEachProduct(orders, paymentSystem, store, entry.getKey(), entry.getValue());
        }
    }

    private void processEachProduct(Map<String, Quantity> orders, PaymentSystem paymentSystem, Store store,
                                    String productName, Quantity quantity) {
        LocalDate now = DateTimes.now().toLocalDate();
        Response response = paymentSystem.pay(productName, quantity, store, now);
        ResponseHandler handler = new ResponseHandler(orders, store, productName, quantity, interactionView);
        handler.handle(response);
    }

    private Map<String, Quantity> parseOrders(List<String> splitText) {
        Map<String, Quantity> orders = new LinkedHashMap<>();
        for (String text : splitText) {
            Matcher matcher = validateFormat(text);
            addOrder(orders, matcher);
        }
        return orders;
    }

    private Promotions makePromotions() {
        List<String> promotionsFromSource = fileReader.readFileFromSource(PROMOTION_FILENAME);
        return new Promotions(promotionsFromSource.stream()
                .filter(input -> !input.startsWith(NAME))
                .map(splitter::split)
                .map(this::createPromotion)
                .toList());
    }

    private Inventories makeInventories() {
        Inventories inventories = createInitialInventories();
        addNoPromotionInventories(inventories);
        return inventories;
    }

    private void addNoPromotionInventories(final Inventories inventories) {
        List<Product> products = getUniqueProducts(inventories);

        for (Product product : products) {
            Inventories productInventories = inventories.findProducts(product.getName());
            if (!hasNoPromotionInventory(productInventories)) {
                inventories.add(new Inventory(product, 0, NULL));
            }
        }
    }

    private List<Product> getUniqueProducts(final Inventories inventories) {
        return inventories.getInventories().stream()
                .map(Inventory::getProduct)
                .distinct()
                .toList();
    }

    private boolean hasNoPromotionInventory(final Inventories inventories) {
        return inventories.getInventories().stream()
                .anyMatch(Inventory::hasNoPromotion);
    }

    private Inventories createInitialInventories() {
        List<String> inventoriesFromSource = fileReader.readFileFromSource(INVENTORY_FILENAME);
        return new Inventories(inventoriesFromSource.stream()
                .filter(input -> !input.startsWith(NAME))
                .map(splitter::split)
                .map(this::createInventory)
                .toList());
    }

    private Matcher validateFormat(final String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(INVALID_FORMAT.getMessageWithPrefix());
        }
        return matcher;
    }

    private void addOrder(final Map<String, Quantity> orders, final Matcher matcher) {
        String productValue = matcher.group(2);
        int quantityValue = Converter.convertToInteger((matcher.group(3)));
        if (quantityValue == 0) {
            throw new IllegalArgumentException(WRONG_INPUT.getMessageWithPrefix());
        }
        orders.put(productValue, orders.getOrDefault(productValue, Quantity.zero()).add(new Quantity(quantityValue)));
    }

    private Promotion createPromotion(final List<String> splittedText) {
        Quantity purchaseQuantity = new Quantity(Converter.convertToInteger(splittedText.get(1)));
        Quantity bonusQuantity = new Quantity(Converter.convertToInteger(splittedText.get(2)));
        LocalDate startDate = Parser.parseToLocalDate(splittedText.get(3));
        LocalDate endDate = Parser.parseToLocalDate(splittedText.get(4));
        return new Promotion(splittedText.get(0), purchaseQuantity, bonusQuantity, startDate, endDate);
    }

    private Inventory createInventory(final List<String> splittedText) {
        Product product = new Product(splittedText.get(0), new BigDecimal(splittedText.get(1)));
        return new Inventory(product,
                Converter.convertToInteger(splittedText.get(2)),
                splittedText.get(3));
    }
}

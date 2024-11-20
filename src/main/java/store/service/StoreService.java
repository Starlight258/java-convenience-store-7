package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.price.Price;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.domain.system.PaymentSystem;
import store.response.Response;
import store.response.ResponseHandler;
import store.util.Converter;
import store.util.DateTimeParser;
import store.util.FileContentParser;
import store.util.OrderTextParser;
import store.util.StoreFileReader;
import store.util.StoreSplitter;
import store.view.InteractionView;

public class StoreService {

    private static final String NULL = "null";

    private final InteractionView interactionView;

    public StoreService(final InteractionView interactionView) {
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
        List<String> splitText = StoreSplitter.split(input);
        Map<String, Quantity> orders = OrderTextParser.parseOrders(splitText);
        inventories.getPurchasedItems(orders);
        return orders;
    }

    public void processPurchase(Map<String, Quantity> orders, PaymentSystem paymentSystem, Store store) {
        for (Entry<String, Quantity> entry : orders.entrySet()) {
            processEachProduct(orders, paymentSystem, store, entry.getKey(), entry.getValue());
        }
    }

    public Price checkMembership(final boolean useMembership, final Membership membership) {
        if (useMembership) {
            return membership.calculateDiscount();
        }
        return Price.zero();
    }

    private void processEachProduct(Map<String, Quantity> orders, PaymentSystem paymentSystem, Store store,
                                    String productName, Quantity quantity) {
        LocalDate now = DateTimes.now().toLocalDate();
        Response response = paymentSystem.pay(productName, quantity, store, now);
        ResponseHandler handler = new ResponseHandler(orders, store, productName, quantity, interactionView);
        handler.handle(response);
    }

    private Promotions makePromotions() {
        List<String> promotionsFromSource = getPromotions();
        return new Promotions(promotionsFromSource.stream()
                .map(StoreSplitter::split)
                .map(this::createPromotion)
                .toList());
    }

    private List<String> getPromotions() {
        List<String> promotionsFromSource = StoreFileReader.readPromotions();
        return FileContentParser.removeHeaders(promotionsFromSource);
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
        List<String> inventories = getInventories();
        return new Inventories(inventories.stream()
                .map(StoreSplitter::split)
                .map(this::createInventory)
                .toList());
    }

    private List<String> getInventories() {
        List<String> inventoriesFromSource = StoreFileReader.readInventories();
        return FileContentParser.removeHeaders(inventoriesFromSource);
    }

    private Promotion createPromotion(final List<String> splittedText) {
        Quantity purchaseQuantity = new Quantity(Converter.convertToInteger(splittedText.get(1)));
        Quantity bonusQuantity = new Quantity(Converter.convertToInteger(splittedText.get(2)));
        LocalDate startDate = DateTimeParser.parseToLocalDate(splittedText.get(3));
        LocalDate endDate = DateTimeParser.parseToLocalDate(splittedText.get(4));
        return new Promotion(splittedText.get(0), purchaseQuantity, bonusQuantity, startDate, endDate);
    }

    private Inventory createInventory(final List<String> splittedText) {
        Product product = new Product(splittedText.get(0), new BigDecimal(splittedText.get(1)));
        return new Inventory(product,
                Converter.convertToInteger(splittedText.get(2)),
                splittedText.get(3));
    }
}

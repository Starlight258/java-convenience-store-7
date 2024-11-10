package store.controller;

import static store.exception.ExceptionMessages.INVALID_FILE_FORMAT;
import static store.exception.ExceptionMessages.INVALID_FORMAT;
import static store.exception.ExceptionMessages.NO_INPUT;
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
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.domain.Store;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.player.PurchaseOrderForms;
import store.domain.price.Price;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.domain.system.PaymentSystem;
import store.response.Response;
import store.response.ResponseStatus;
import store.support.StoreFormatter;
import store.support.StoreSplitter;
import store.util.Converter;
import store.util.Parser;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    public static final String INVENTORY_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/products.md";
    public static final String PROMOTION_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/promotions.md";
    public static final String REGEX = "^\\[((\\w*\\W*)-(\\d+))\\]$";
    public static final Pattern PATTERN = Pattern.compile(REGEX);
    public static final String YES = "Y";
    public static final String NO = "N";

    private final InputView inputView;
    private final OutputView outputView;
    private final StoreSplitter splitter;
    private final StoreFormatter formatter;

    public StoreController(final InputView inputView, final OutputView outputView, final StoreSplitter splitter,
                           final StoreFormatter formatter) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.splitter = splitter;
        this.formatter = formatter;
    }

    public void process() {
        Inventories inventories = null;
        Promotions promotions = null;
        try {
            inventories = makeInventories();
            promotions = makePromotions();
        } catch (IllegalArgumentException | IllegalStateException e) {
            outputView.showExceptionMessage(e.getMessage());
        }
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);

        while (true) {
            try {
                outputView.showStartMessage();
                showInventories(inventories);
                PurchaseOrderForms purchaseOrderForms = getPurchasedItems(inventories);
                convenienceStore(purchaseOrderForms, paymentSystem);
                outputView.showAdditionalPurchase();
                String line = readYOrN();
                if (line.equals(NO)) {
                    return;
                }
                outputView.showBlankLine();
            } catch (NoSuchElementException e) {
                outputView.showExceptionMessage(NO_INPUT.getErrorMessage());
                return;
            } catch (IllegalArgumentException | IllegalStateException exception) {
                outputView.showExceptionMessage(exception.getMessage());
            }
        }
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

    private void convenienceStore(final PurchaseOrderForms purchaseOrderForms, final PaymentSystem paymentSystem) {
        Map<String, Quantity> purchasedItems = purchaseOrderForms.getProductsToBuy();
        Receipt receipt = new Receipt(new LinkedHashMap<>(), new LinkedHashMap<>());
        Membership membership = new Membership(new LinkedHashMap<>());
        Store store = new Store(receipt, membership);
        for (Entry<String, Quantity> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            Quantity quantity = entry.getValue();
            LocalDate now = DateTimes.now().toLocalDate();
            Response response = paymentSystem.canBuy(productName, quantity, store, now);
            checkResponse(purchaseOrderForms, store, productName, quantity, response);
        }
        Price membershipPrice = checkMemberShip(membership);
        showResultPrice(receipt, membershipPrice);
    }

    private PurchaseOrderForms getPurchasedItems(final Inventories inventories) {
        outputView.showCommentOfPurchase();
        while (true) {
            try {
                PurchaseOrderForms purchasedItems = promptProductNameAndQuantity();
                inventories.getPurchasedItems(purchasedItems);  // 구매할 상품의 이름
                return purchasedItems;
            } catch (IllegalArgumentException exception) {
                outputView.showExceptionMessage(exception.getMessage());
            } catch (IllegalStateException exception) {
                outputView.showExceptionMessage(INVALID_FORMAT.getErrorMessage());
            } catch (OutOfMemoryError exception) {
                outputView.showExceptionMessage(WRONG_INPUT.getErrorMessage());
            }
        }
    }

    private void showResultPrice(final Receipt receipt, final Price membershipPrice) {
        showResult(receipt, membershipPrice);
    }

    private Price checkMemberShip(final Membership membership) {
        outputView.showCommentOfMemberShip();
        String line = readYOrN();
        if (line.equals(NO)) {
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
        int blankLength =
                String.valueOf(totalPurchasePrice.getPrice()).length() - String.valueOf(priceToPay.getPrice()).length();
        outputView.showTotalPrice(totalPurchases.getKey().getQuantity(), totalPurchasePrice.getPrice());
        outputView.showPromotionDiscountPrice(receipt.getPromotionDiscountPrice().getPrice());
        outputView.showMemberShipDiscountPrice(membershipPrice.getPrice());
        outputView.showMoneyToPay(priceToPay.getPrice(), blankLength);
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

    private void checkResponse(final PurchaseOrderForms purchaseOrderForms,
                               final Store store, final String productName,
                               final Quantity quantity,
                               final Response response) {
        if (response.status() == ResponseStatus.BUY_WITH_NO_PROMOTION) {
            return;
        }
        if (response.status() == ResponseStatus.BUY_WITH_PROMOTION) {
            Quantity bonusQuantity = response.bonusQuantity();
            store.noteBonusProduct(response.inventory().getProduct(), bonusQuantity);
            return;
        }
        if (response.status() == ResponseStatus.OUT_OF_STOCK) {
            Quantity outOfStockQuantity = outOfStock(productName, response, store, quantity);
            if (outOfStockQuantity.isMoreThan(Quantity.zero())) { // 구매안함
                purchaseOrderForms.put(productName, quantity.subtract(outOfStockQuantity));
            }
            return;
        }
        Quantity canGetMoreQuantity = canGetBonus(store, productName, response);
        Product product = response.inventory().getProduct();
        purchaseOrderForms.put(productName, quantity);
        store.notePurchaseProduct(product, quantity);
        if (canGetMoreQuantity.isMoreThan(Quantity.zero())) {
            purchaseOrderForms.put(productName, quantity.add(canGetMoreQuantity));
            store.notePurchaseProduct(product, canGetMoreQuantity);
        }
    }

    public PurchaseOrderForms promptProductNameAndQuantity() {
        PurchaseOrderForms purchasedItems = new PurchaseOrderForms(new LinkedHashMap<>());
        String input = inputView.readLine();
        List<String> splitText = splitter.split(input);
        return addPurchasedItems(purchasedItems, splitText);
    }

    private PurchaseOrderForms addPurchasedItems(final PurchaseOrderForms purchaseOrderForms,
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
            purchaseOrderForms.put(productValue, new Quantity(quantityValue));
        }
        return purchaseOrderForms;
    }

    private String readYOrN() {
        while (true) {
            try {
                String intent = inputView.readLine();
                if (intent.equals(YES) || intent.equals(NO)) {
                    return intent;
                }
                throw new IllegalArgumentException(WRONG_INPUT.getErrorMessage());
            } catch (IllegalArgumentException exception) {
                outputView.showExceptionMessage(exception.getMessage());
            }
        }
    }

    private Quantity outOfStock(final String productName,
                                final Response response, final Store store,
                                final Quantity quantity) {
        Quantity totalBonusQuantity = response.bonusQuantity();
        store.noteBonusProduct(response.inventory().getProduct(), totalBonusQuantity);
        Quantity noPromotionQuantityOfResponse = response.noPromotionQuantity();
        outputView.showPromotionDiscount(productName, noPromotionQuantityOfResponse.getQuantity());
        String intent = readYOrN();
        Product product = response.inventory().getProduct();
        if (intent.equals(NO)) {
            store.notePurchaseProduct(product, quantity.subtract(noPromotionQuantityOfResponse));
            return noPromotionQuantityOfResponse;
        }
        store.noteNoPromotionProduct(product, quantity);
        return Quantity.zero();
    }

    private Quantity canGetBonus(final Store store, final String productName, final Response response) {
        if (response.status() == ResponseStatus.CAN_GET_BONUS) {
            Quantity bonusQuantity = response.bonusQuantity();
            Quantity canGetMoreQuantity = response.canGetMoreQuantity();
            outputView.showFreeQuantity(productName, canGetMoreQuantity.getQuantity());
            String intent = readYOrN();
            Product product = response.inventory().getProduct();
            if (intent.equals(YES)) {
                store.noteBonusProduct(product, bonusQuantity);
                return canGetMoreQuantity;
            }
            store.noteBonusProduct(product, bonusQuantity.subtract(canGetMoreQuantity));
        }
        return Quantity.zero();
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

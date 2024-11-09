package store.controller;

import static store.exception.ExceptionMessage.INVALID_FILE_FORMAT;
import static store.exception.ExceptionMessage.INVALID_FORMAT;
import static store.exception.ExceptionMessage.NO_INPUT;
import static store.exception.ExceptionMessage.WRONG_INPUT;

import camp.nextstep.edu.missionutils.DateTimes;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.player.PurchaseOrderForms;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
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
                Map<String, Integer> purchasedItems = getPurchasedItems(inventories);
                PurchaseOrderForms purchaseOrderForms = new PurchaseOrderForms(purchasedItems);
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
        Promotions promotions = addPromotion(promotionsFromSource);
        return promotions;
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
            String message = formatter.makeInventoryMessage(inventory.getQuantity(), inventory.getPromotionName(),
                    inventory.getProductName(), inventory.getProductPrice());
            outputView.showMessage(message);
        }
    }

    private void convenienceStore(final PurchaseOrderForms purchaseOrderForms, final PaymentSystem paymentSystem) {
        Map<String, Integer> purchasedItems = purchaseOrderForms.getProductsToBuy();
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        for (Entry<String, Integer> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            LocalDate now = DateTimes.now().toLocalDate();
            Response response = paymentSystem.canBuy(productName, quantity, receipt, now, membership);
            checkResponse(purchaseOrderForms, receipt, productName, quantity, response, membership);
        }
        BigDecimal membershipPrice = checkMemberShip(membership);
        showResultPrice(receipt, membershipPrice);
    }

    private Map<String, Integer> getPurchasedItems(final Inventories inventories) {
        outputView.showCommentOfPurchase();
        while (true) {
            try {
                Map<String, Integer> purchasedItems = promptProductNameAndQuantity();
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

    private void showResultPrice(final Receipt receipt, final BigDecimal membershipPrice) {
        showResult(receipt, membershipPrice);
    }

    private BigDecimal checkMemberShip(final Membership membership) {
        outputView.showCommentOfMemberShip();
        String line = readYOrN();
        if (line.equals(NO)) {
            return BigDecimal.ZERO;
        }
        return membership.calculateDiscount();
    }

    private void showResult(final Receipt receipt, final BigDecimal membershipPrice) {
        showPurchasedProducts(receipt);
        showBonus(receipt);
        showReceipt(receipt, membershipPrice);
    }

    private void showPurchasedProducts(final Receipt receipt) {
        outputView.showCommentOfInventory();
        for (Entry<Product, Integer> entry : receipt.getPurchasedProducts().entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue();
            BigDecimal totalPrice = entry.getKey().getPrice().multiply(BigDecimal.valueOf(quantity));
            outputView.showInventory(name, quantity, totalPrice);
        }
    }

    private void showReceipt(final Receipt receipt, final BigDecimal membershipPrice) {
        outputView.showReceiptStartMark();
        Entry<Integer, BigDecimal> totalPurchase = receipt.getTotalPurchase();
        BigDecimal priceToPay = receipt.getPriceToPay(totalPurchase.getValue(), membershipPrice);
        BigDecimal totalPurchaseValue = totalPurchase.getValue();
        int blankLength = String.valueOf(totalPurchaseValue).length() - String.valueOf(priceToPay).length();
        outputView.showTotalPrice(totalPurchase.getKey(), totalPurchaseValue);
        outputView.showPromotionDiscountPrice(receipt.getPromotionDiscountPrice());
        outputView.showMemberShipDiscountPrice(membershipPrice);
        outputView.showMoneyToPay(priceToPay, blankLength);
    }

    private void showBonus(final Receipt receipt) {
        outputView.showBonus();
        for (Entry<Product, Integer> entry : receipt.getBonusProducts().entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue();
            outputView.showBonusProduct(name, quantity);
        }
    }

    private void checkResponse(final PurchaseOrderForms purchaseOrderForms,
                               final Receipt receipt, final String productName,
                               final int quantity,
                               final Response response, final Membership membership) {
        if (response.status() == ResponseStatus.BUY_WITH_NO_PROMOTION) {
            return;
        }
        if (response.status() == ResponseStatus.BUY_WITH_PROMOTION) {
            int bonusQuantity = response.bonusQuantity();
            receipt.addBonusProducts(response.inventory().getProduct(), bonusQuantity);
            return;
        }
        if (response.status() == ResponseStatus.OUT_OF_STOCK) {
            int outOfStockQuantity = outOfStock(productName, response, receipt, quantity, membership);
            if (outOfStockQuantity > 0) { // 구매안함
                purchaseOrderForms.put(productName, quantity - outOfStockQuantity);
            }
            return;
        }
        int canGetMoreQuantity = canGetBonus(receipt, productName, response);
        Product product = response.inventory().getProduct();
        purchaseOrderForms.put(productName, quantity);
        receipt.purchaseProducts(product, quantity);
        if (canGetMoreQuantity > 0) {
            purchaseOrderForms.put(productName, quantity + canGetMoreQuantity);
            receipt.purchaseProducts(product, canGetMoreQuantity);
        }
    }

    public Map<String, Integer> promptProductNameAndQuantity() {
        Map<String, Integer> purchasedItems = new HashMap<>();
        String input = inputView.readLine();
        List<String> splitText = splitter.split(input);
        addPurchasedItems(purchasedItems, splitText);
        return purchasedItems;
    }

    private void addPurchasedItems(final Map<String, Integer> purchasedItems, final List<String> splittedText) {
        for (String text : splittedText) {
            Matcher matcher = PATTERN.matcher(text);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(INVALID_FORMAT.getErrorMessage());
            }
            purchasedItems.put(matcher.group(2), Converter.convertToInteger((matcher.group(3))));
        }
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

    private int outOfStock(final String productName,
                           final Response response, final Receipt receipt,
                           final int quantity, final Membership membership) {
        int totalBonusQuantity = response.bonusQuantity();
        receipt.addBonusProducts(response.inventory().getProduct(), totalBonusQuantity);
        int noPromotionQuantityOfResponse = response.noPromotionQuantity();
        outputView.showPromotionDiscount(productName, noPromotionQuantityOfResponse);
        String intent = readYOrN();
        Product product = response.inventory().getProduct();
        if (intent.equals(NO)) {
            receipt.purchaseProducts(product, quantity - noPromotionQuantityOfResponse);
            return noPromotionQuantityOfResponse;
        }
        receipt.purchaseProducts(product, quantity);
        membership.addNoPromotionProduct(product, quantity);
        return 0;
    }

    private int canGetBonus(final Receipt receipt, final String productName,
                            final Response response) {
        if (response.status() == ResponseStatus.CAN_GET_BONUS) {
            int bonusQuantity = response.bonusQuantity();
            int canGetMoreQuantity = response.canGetMoreQuantity();
            outputView.showFreeQuantity(productName, canGetMoreQuantity);
            String intent = readYOrN();
            Product product = response.inventory().getProduct();
            if (intent.equals(YES)) {
                receipt.addBonusProducts(product, bonusQuantity);
                return canGetMoreQuantity;
            }
            receipt.addBonusProducts(product, bonusQuantity - canGetMoreQuantity);
        }
        return 0;
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
        int purchaseQuantity = Converter.convertToInteger(splittedText.get(1));
        int bonusQuantity = Converter.convertToInteger(splittedText.get(2));
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

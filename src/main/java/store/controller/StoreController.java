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
import store.Converter;
import store.Inventories;
import store.Inventory;
import store.Parser;
import store.PaymentSystem;
import store.Product;
import store.Promotion;
import store.Promotions;
import store.Receipt;
import store.Response;
import store.ResponseStatus;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    public static final String INVENTORY_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/products.md";
    public static final String PROMOTION_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/promotions.md";
    public static final String REGEX = "^\\[((\\w*\\W*)-(\\d+))\\]$";
    public static final Pattern PATTERN = Pattern.compile(REGEX);
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String NULL = "null";
    public static final String QUANTITY_UNIT = "개 ";
    public static final String NO_STOCK = "재고 없음 ";

    private final InputView inputView;
    private final OutputView outputView;

    public StoreController(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void process() {
        Inventories inventories = null;
        Promotions promotions;
        PaymentSystem paymentSystem = null;

        try {
            outputView.showStartMessage();
            inventories = addInventory();
            showInventories(inventories);
            promotions = addPromotion();
            paymentSystem = new PaymentSystem(inventories, promotions);
        } catch (IOException ignored) {
            outputView.showExceptionMessage(INVALID_FILE_FORMAT.getErrorMessage());
        }
        convenienceStore(inventories, paymentSystem);
    }

    private void convenienceStore(final Inventories inventories, final PaymentSystem paymentSystem) {
        while (true) {
            try {
                LocalDate now = DateTimes.now().toLocalDate();
                convenienceStore(inventories, paymentSystem, now);
                outputView.showAdditionalPurchase();
                String line = readYOrN();
                if (line.equals(NO)) {
                    return;
                }
                outputView.showBlankLine();
                outputView.showStartMessage();
                showInventories(inventories);
            } catch (NoSuchElementException e) {
                outputView.showExceptionMessage(NO_INPUT.getErrorMessage());
                return;
            } catch (IllegalArgumentException | IllegalStateException exception) {
                outputView.showExceptionMessage(exception.getMessage());
            }
        }
    }

    private void showInventories(final Inventories inventories) {
        for (Inventory inventory : inventories.getInventories()) {
            int quantity = inventory.getQuantity();
            String quanityText = quantity + QUANTITY_UNIT;
            if (quantity == 0) {
                quanityText = NO_STOCK;
            }
            String promotionName = inventory.getPromotionName();
            String promotionNameText = promotionName;
            if (promotionName.equals(NULL)) {
                promotionNameText = "";
            }
            outputView.showProduct(inventory.getProductName(), inventory.getProductPrice(), quanityText,
                    promotionNameText);
        }
    }

    private void convenienceStore(final Inventories inventories, final PaymentSystem paymentSystem,
                                  final LocalDate now) {
        Map<String, BigDecimal> totalNoPromotionPrice = new HashMap<>();
        Map<Product, Integer> purchasedProducts = new HashMap<>();
        Map<Product, Integer> bonusItems = new HashMap<>();
        Map<String, Integer> purchasedItems = getPurchasedItems(inventories);
        checkPromotion(inventories, paymentSystem, now, purchasedItems, totalNoPromotionPrice, bonusItems,
                purchasedProducts);
        BigDecimal membershipPrice = checkMemberShip(paymentSystem, totalNoPromotionPrice);
        showResult(purchasedProducts, bonusItems, membershipPrice);
    }

    private Map<String, Integer> getPurchasedItems(final Inventories inventories) {
        outputView.showCommentOfPurchase();
        while (true) {
            try {
                Map<String, Integer> purchasedItems = promptProductNameAndQuantity();
                inventories.getPurchasedItems(purchasedItems, inventories);  // 구매할 상품의 이름
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

    private void showResult(final Map<Product, Integer> purchasedProducts,
                            final Map<Product, Integer> bonusItems, final BigDecimal membershipPrice) {
        Receipt receipt = new Receipt(purchasedProducts, bonusItems, membershipPrice);
        showResult(purchasedProducts, bonusItems, receipt);
    }

    private BigDecimal checkMemberShip(final PaymentSystem paymentSystem,
                                       final Map<String, BigDecimal> totalNoPromotionPrice) {
        outputView.showCommentOfMemberShip();
        String line = readYOrN();
        return paymentSystem.checkMembership(line, totalNoPromotionPrice);
    }

    private void showResult(final Map<Product, Integer> purchasedProducts,
                            final Map<Product, Integer> bonusItems, final Receipt receipt) {
        showPurchasedProducts(purchasedProducts);
        showBonus(bonusItems);
        showReceipt(receipt);
    }

    private void showReceipt(final Receipt receipt) {
        outputView.showReceiptStartMark();
        Entry<Integer, BigDecimal> totalPurchase = receipt.getTotalPurchase();
        BigDecimal priceToPay = receipt.getPriceToPay();
        BigDecimal totalPurchaseValue = totalPurchase.getValue();
        int blankLength = String.valueOf(totalPurchaseValue).length() - String.valueOf(priceToPay).length();
        outputView.showTotalPrice(totalPurchase.getKey(), totalPurchaseValue);
        outputView.showPromotionDiscountPrice(receipt.getPromotionDiscountPrice());
        outputView.showMemberShipDiscountPrice(receipt.getMemberShipDiscountPrice());
        outputView.showMoneyToPay(priceToPay, blankLength);
    }

    private void showBonus(final Map<Product, Integer> bonusItems) {
        outputView.showBonus();
        for (Entry<Product, Integer> entry : bonusItems.entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue();
            outputView.showBonusProduct(name, quantity);
        }
    }

    private void showPurchasedProducts(final Map<Product, Integer> purchasedProducts) {
        outputView.showCommentOfInventory();
        for (Entry<Product, Integer> entry : purchasedProducts.entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue();
            BigDecimal totalPrice = entry.getKey().getPrice().multiply(BigDecimal.valueOf(quantity));
            outputView.showInventory(name, quantity, totalPrice);
        }
    }

    private void checkPromotion(final Inventories inventories, final PaymentSystem paymentSystem,
                                final LocalDate now,
                                final Map<String, Integer> purchasedItems,
                                final Map<String, BigDecimal> totalNoPromotionPrice,
                                final Map<Product, Integer> bonusItems,
                                final Map<Product, Integer> purchasedProducts) {
        for (Entry<String, Integer> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            Inventories sameProductInventories = inventories.findProducts(productName);
            Response response = paymentSystem.canBuy(sameProductInventories, quantity, now, purchasedProducts);
            if (response.status() == ResponseStatus.BUY_WITH_NO_PROMOTION) {
                totalNoPromotionPrice.put(productName, response.totalPrice());
                continue;
            }
            if (response.status() == ResponseStatus.BUY_WITH_PROMOTION) {
                int bonusQuantity = response.bonusQuantity();
                bonusItems.put(response.inventory().getProduct(), bonusQuantity);
                continue;
            }
            if (response.status() == ResponseStatus.OUT_OF_STOCK) {
                int outOfStockQuantity = outOfStock(bonusItems, productName, response, purchasedProducts, quantity);
                if (outOfStockQuantity > 0) {
                    purchasedItems.put(productName, quantity - outOfStockQuantity);
                }
                continue;
            }
            int canGetMoreQuantity = canGetBonus(bonusItems, productName, response);
            Product product = response.inventory().getProduct();
            purchasedItems.put(productName, quantity);
            purchasedProducts.put(product, purchasedProducts.getOrDefault(product, 0) + quantity);
            if (canGetMoreQuantity > 0) {
                purchasedItems.put(productName, quantity + canGetMoreQuantity);
                purchasedProducts.put(product, purchasedProducts.getOrDefault(product, 0) + canGetMoreQuantity);
            }
        }
    }

    public Map<String, Integer> promptProductNameAndQuantity() {
        Map<String, Integer> purchasedItems = new HashMap<>();
        String input = inputView.readLine();
        String[] splittedText = input.split(",");
        addPurchasedItems(purchasedItems, splittedText);
        return purchasedItems;
    }

    private void addPurchasedItems(final Map<String, Integer> purchasedItems, final String[] splittedText) {
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

    private int outOfStock(final Map<Product, Integer> bonusItems, final String productName,
                           final Response response, Map<Product, Integer> purchasedProducts,
                           final int quantity) {
        int totalBonusQuantity = response.bonusQuantity();
        bonusItems.put(response.inventory().getProduct(), totalBonusQuantity);
        int noPromotionQuantityOfResponse = response.noPromotionQuantity();
        outputView.showPromotionDiscount(productName, noPromotionQuantityOfResponse);
        String intent = readYOrN();
        Product product = response.inventory().getProduct();
        if (intent.equals(NO)) {
            purchasedProducts.put(product,
                    purchasedProducts.getOrDefault(product, 0) + quantity - noPromotionQuantityOfResponse);
            return noPromotionQuantityOfResponse;
        }
        purchasedProducts.put(product,
                purchasedProducts.getOrDefault(product, 0) + quantity);
        return 0;
    }

    private int canGetBonus(final Map<Product, Integer> bonusItems, final String productName,
                            final Response response) {
        if (response.status() == ResponseStatus.CAN_GET_BONUS) {
            int bonusQuantity = response.bonusQuantity();
            int canGetMoreQuantity = response.canGetMoreQuantity();
            outputView.showFreeQuantity(productName, canGetMoreQuantity);
            String intent = readYOrN();
            Product product = response.inventory().getProduct();
            if (intent.equals(YES)) {
                bonusItems.put(product, bonusQuantity);
                return canGetMoreQuantity;
            }
            bonusItems.put(product, bonusQuantity - canGetMoreQuantity);
        }
        return 0;
    }

    private Promotions addPromotion() throws IOException {
        List<Promotion> promotions = new ArrayList<>();
        List<String> inputs = inputView.readFile(PROMOTION_FILENAME);
        for (String input : inputs) {
            if (input.startsWith("name")) {
                continue;
            }
            String[] split = input.split(",", -1);
            Promotion promotion = getPromotion(split);
            promotions.add(promotion);
        }
        return new Promotions(promotions);
    }

    private Promotion getPromotion(final String[] split) {
        int purchaseQuantity = Converter.convertToInteger(split[1]);
        int bonusQuantity = Converter.convertToInteger(split[2]);
        LocalDate startDate = Parser.parseToLocalDate(split[3]);
        LocalDate endDate = Parser.parseToLocalDate(split[4]);
        return new Promotion(split[0], purchaseQuantity, bonusQuantity, startDate, endDate);
    }

    private Inventories addInventory()
            throws IOException {
        List<Inventory> inventories = new ArrayList<>();
        List<String> inputs = inputView.readFile(INVENTORY_FILENAME);
        for (String input : inputs) {
            if (input.startsWith("name")) {
                continue;
            }
            String[] split = input.split(",", -1);
            Product product = new Product(split[0], new BigDecimal(split[1]));
            Inventory inventory = new Inventory(product, Converter.convertToInteger(split[2]), split[3]);
            inventories.add(inventory);
        }
        return new Inventories(inventories);
    }
}

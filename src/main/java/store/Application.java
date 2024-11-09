package store;

import static camp.nextstep.edu.missionutils.Console.readLine;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.io.BufferedReader;
import java.io.FileReader;
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

public class Application {

    public static final String INVENTORY_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/products.md";
    public static final String PROMOTION_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/promotions.md";
    public static final int FORMAT_SIZE = 12;
    public static final String REGEX = "^\\[((\\w*\\W*)-(\\d+))\\]$";
    public static final Pattern PATTERN = Pattern.compile(REGEX);

    public static void main(String[] args) {
        Inventories inventories = null;
        Promotions promotions;
        PaymentSystem paymentSystem = null;

        try {
            printStartMessage();
            inventories = addInventory();
            showInventories(inventories);
            promotions = addPromotion();
            paymentSystem = new PaymentSystem(inventories, promotions);
        } catch (IOException ignored) {
            System.out.println("[ERROR] 파일 형식이 잘못되었습니다.");
        }

        convenienceStore(inventories, paymentSystem);
        Console.close();
    }

    private static void convenienceStore(final Inventories inventories, final PaymentSystem paymentSystem) {
        while (true) {
            try {
                LocalDate now = DateTimes.now().toLocalDate();
                convenienceStore(inventories, paymentSystem, now);
                System.out.println(System.lineSeparator() + "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
                String line = readYOrN();
                if (line.equals("N")) {
                    return;
                }
                System.out.println();
                printStartMessage();
                showInventories(inventories);
            } catch (NoSuchElementException e) {
                System.out.println("[ERROR] 아무것도 입력하지 않았습니다.");
                return;
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private static void showInventories(final Inventories inventories) {
        for (Inventory inventory : inventories.getInventories()) {
            int quantity = inventory.getQuantity();
            String quanityText = quantity + "개 ";
            if (quantity == 0) {
                quanityText = "재고 없음 ";
            }
            String promotionName = inventory.getPromotionName();
            String promotionNameText = promotionName;
            if (promotionName.equals("null")) {
                promotionNameText = "";
            }
            System.out.printf("- %s" + " %,.0f원 %s%s\n", inventory.getProductName(), inventory.getProductPrice(),
                    quanityText, promotionNameText);
        }
    }

    private static void convenienceStore(final Inventories inventories, final PaymentSystem paymentSystem,
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

    private static Map<String, Integer> getPurchasedItems(final Inventories inventories) {
        System.out.println(System.lineSeparator() + "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        while (true) {
            try {
                Map<String, Integer> purchasedItems = promptProductNameAndQuantity();
                inventories.getPurchasedItems(purchasedItems, inventories);  // 구매할 상품의 이름
                return purchasedItems;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            } catch (IllegalStateException exception) {
                System.out.println("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            } catch (OutOfMemoryError exception) {
                System.out.println("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
            }
        }
    }

    private static void showResult(final Map<Product, Integer> purchasedProducts,
                                   final Map<Product, Integer> bonusItems, final BigDecimal membershipPrice) {
        Receipt receipt = new Receipt(purchasedProducts, bonusItems, membershipPrice);
        showResult(purchasedProducts, bonusItems, receipt);
    }

    private static BigDecimal checkMemberShip(final PaymentSystem paymentSystem,
                                              final Map<String, BigDecimal> totalNoPromotionPrice) {
        System.out.println(System.lineSeparator() + "멤버십 할인을 받으시겠습니까? (Y/N)");
        String line = readYOrN();
        return paymentSystem.checkMembership(line, totalNoPromotionPrice);
    }

    private static void showResult(final Map<Product, Integer> purchasedProducts,
                                   final Map<Product, Integer> bonusItems, final Receipt receipt) {
        showPurchasedProducts(purchasedProducts);
        showBonus(bonusItems);
        showReceipt(receipt);
    }

    private static void showReceipt(final Receipt receipt) {
        System.out.println("====================================");
        Entry<Integer, BigDecimal> totalPurchase = receipt.getTotalPurchase();
        BigDecimal priceToPay = receipt.getPriceToPay();
        BigDecimal totalPurchaseValue = totalPurchase.getValue();
        int blankLength = String.valueOf(totalPurchaseValue).length() - String.valueOf(priceToPay).length();
        String blank = " ".repeat(Math.max(0, blankLength));
        System.out.printf(
                convert("총구매액", FORMAT_SIZE) + " \t%d \t%,.0f"
                        + System.lineSeparator(),
                totalPurchase.getKey(), totalPurchaseValue);

        System.out.printf(convert("행사할인", FORMAT_SIZE) + " \t\t-%,.0f\n",
                receipt.getPromotionDiscountPrice());
        System.out.printf(convert("멤버십할인", FORMAT_SIZE) + " \t\t-%,.0f\n",
                receipt.getMemberShipDiscountPrice());
        System.out.printf(convert("내실돈", FORMAT_SIZE) + " \t\t" + blank + "%,.0f\n", priceToPay);

    }

    private static int getKoreanCount(String text) {
        int cnt = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) >= '가' && text.charAt(i) <= '힣') {
                cnt++;
            }
        }
        return cnt;
    }

    public static String convert(String word, int formatSize) {
        String formatter = String.format("%%-%ds", formatSize - getKoreanCount(word));
        return String.format(formatter, word);
    }

    private static void showBonus(final Map<Product, Integer> bonusItems) {
        System.out.println("=============증\t정===============");
        for (Entry<Product, Integer> entry : bonusItems.entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue();
            System.out.printf(convert(name, FORMAT_SIZE) + "\t%d \t" + System.lineSeparator(), quantity);
        }
    }

    private static void showPurchasedProducts(final Map<Product, Integer> purchasedProducts) {
        System.out.println(System.lineSeparator() + "==============W 편의점================\n"
                + "상품명\t\t수량\t금액");
        for (Entry<Product, Integer> entry : purchasedProducts.entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue();
            BigDecimal totalPrice = entry.getKey().getPrice().multiply(BigDecimal.valueOf(quantity));
            System.out.printf(
                    convert(name, FORMAT_SIZE) + " \t%d \t%,.0f"
                            + System.lineSeparator(), quantity, totalPrice);
        }
    }

    private static void checkPromotion(final Inventories inventories, final PaymentSystem paymentSystem,
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

    public static Map<String, Integer> promptProductNameAndQuantity() {
        Map<String, Integer> purchasedItems = new HashMap<>();
        String input = readLine();
        String[] splittedText = input.split(",");
        addPurchasedItems(purchasedItems, splittedText);
        return purchasedItems;
    }

    private static void addPurchasedItems(final Map<String, Integer> purchasedItems, final String[] splittedText) {
        for (String text : splittedText) {
            Matcher matcher = PATTERN.matcher(text);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            }
            purchasedItems.put(matcher.group(2), Converter.convertToInteger((matcher.group(3))));
        }
    }

    private static String readYOrN() {
        while (true) {
            try {
                String intent = readLine();
                if (intent.equals("Y") || intent.equals("N")) {
                    return intent;
                }
                throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private static int outOfStock(final Map<Product, Integer> bonusItems, final String productName,
                                  final Response response, Map<Product, Integer> purchasedProducts,
                                  final int quantity) {
        int totalBonusQuantity = response.bonusQuantity();
        bonusItems.put(response.inventory().getProduct(), totalBonusQuantity);
        int noPromotionQuantityOfResponse = response.noPromotionQuantity();
        System.out.printf(System.lineSeparator() + "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)"
                        + System.lineSeparator(),
                productName, noPromotionQuantityOfResponse);
        String intent = readYOrN();
        Product product = response.inventory().getProduct();
        if (intent.equals("N")) {
            purchasedProducts.put(product,
                    purchasedProducts.getOrDefault(product, 0) + quantity - noPromotionQuantityOfResponse);
            return noPromotionQuantityOfResponse;
        }
        purchasedProducts.put(product,
                purchasedProducts.getOrDefault(product, 0) + quantity);
        return 0;
    }

    private static int canGetBonus(final Map<Product, Integer> bonusItems, final String productName,
                                   final Response response) {
        if (response.status() == ResponseStatus.CAN_GET_BONUS) {
            int bonusQuantity = response.bonusQuantity();
            int canGetMoreQuantity = response.canGetMoreQuantity();
            System.out.printf(System.lineSeparator() + "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)"
                    + System.lineSeparator(), productName, canGetMoreQuantity);
            String intent = readYOrN();
            Product product = response.inventory().getProduct();
            if (intent.equals("Y")) {
                bonusItems.put(product, bonusQuantity);
                return canGetMoreQuantity;
            }
            bonusItems.put(product, bonusQuantity - canGetMoreQuantity);
        }
        return 0;
    }

    private static void printStartMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.");
        System.out.println();
    }

    private static Promotions addPromotion() throws IOException {
        List<Promotion> promotions = new ArrayList<>();
        List<String> inputs = input(PROMOTION_FILENAME);
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

    private static Promotion getPromotion(final String[] split) {
        int purchaseQuantity = Converter.convertToInteger(split[1]);
        int bonusQuantity = Converter.convertToInteger(split[2]);
        LocalDate startDate = Parser.parseToLocalDate(split[3]);
        LocalDate endDate = Parser.parseToLocalDate(split[4]);
        return new Promotion(split[0], purchaseQuantity, bonusQuantity, startDate, endDate);
    }

    private static Inventories addInventory()
            throws IOException {
        List<Inventory> inventories = new ArrayList<>();
        List<String> inputs = input(INVENTORY_FILENAME);
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

    private static void print(final String[] split) {
        String output = "- " + split[0] + " " + split[1] + " ";
        if (split[2].equals("0")) {
            output += "재고 없음 " + split[3];
            System.out.println(output);
            return;
        }
        output += split[2] + " " + split[3];
        System.out.println(output);
    }

    private static List<String> input(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<String> inputs = new ArrayList<>();
        while (true) {
            String input = br.readLine();
            if (input == null) {
                break;
            }
            inputs.add(input);
        }
        br.close();
        return inputs;
    }
}

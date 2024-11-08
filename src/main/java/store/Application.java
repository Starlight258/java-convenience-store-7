package store;

import static camp.nextstep.edu.missionutils.Console.readLine;

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
import java.util.Objects;

public class Application {

    public static final String INVENTORY_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/products.md";
    public static final String PROMOTION_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/promotions.md";
    public static final int FORMAT_SIZE = 12;

    public static void main(String[] args) throws IOException {
        printStartMessage();
        Inventories inventories = addInventory();
        showInventories(inventories);
        Promotions promotions = addPromotion();
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = DateTimes.now().toLocalDate();
        convenienceStore(inventories, paymentSystem, now);
        while (true) {
            System.out.println(System.lineSeparator() + "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
            String line = readLine();
            if (line.equals("Y")) {
                System.out.println();
                printStartMessage();
                showInventories(inventories);
                convenienceStore(inventories, paymentSystem, now);
                continue;
            }
            break;
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
        Map<String, Integer> purchasedItems = getStringIntegerMap();  // 구매할 상품의 이름
        Map<String, BigDecimal> totalNoPromotionPrice = new HashMap<>();
        Map<Product, Integer> purchasedProducts = new HashMap<>();
        Map<Product, Integer> bonusItems = new HashMap<>();
        checkPromotion(paymentSystem, now, purchasedItems, totalNoPromotionPrice, bonusItems, purchasedProducts);
        BigDecimal membershipPrice = checkMemberShip(paymentSystem, totalNoPromotionPrice);
        showResult(inventories, purchasedProducts, bonusItems, membershipPrice);
    }

    private static void showResult(final Inventories inventories, final Map<Product, Integer> purchasedProducts,
                                   final Map<Product, Integer> bonusItems, final BigDecimal membershipPrice) {
        Receipt receipt = new Receipt(purchasedProducts, bonusItems, membershipPrice);
        showResult(purchasedProducts, bonusItems, receipt);
    }

    private static BigDecimal checkMemberShip(final PaymentSystem paymentSystem,
                                              final Map<String, BigDecimal> totalNoPromotionPrice) {
        System.out.println(System.lineSeparator() + "멤버십 할인을 받으시겠습니까? (Y/N)");
        String line = readLine();
        BigDecimal membershipPrice = paymentSystem.checkMembership(line, totalNoPromotionPrice);
        return membershipPrice;
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
        StringBuilder blank = new StringBuilder();
        for (int i = 0; i < blankLength; i++) {
            blank.append(" ");
        }
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

    private static void checkPromotion(final PaymentSystem paymentSystem, final LocalDate now,
                                       final Map<String, Integer> purchasedItems,
                                       final Map<String, BigDecimal> totalNoPromotionPrice,
                                       final Map<Product, Integer> bonusItems,
                                       final Map<Product, Integer> purchasedProducts) {
        for (Entry<String, Integer> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            Response response = paymentSystem.canBuy(productName, quantity, now, purchasedProducts);
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

    private static String[] promptProductNameAndQuantity() {
        System.out.println(System.lineSeparator() + "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String input = readLine();
        input = input.replace("[", "");
        input = input.replace("]", "");
        return input.split(",");
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
        String intent = readLine();
        Product product = response.inventory().getProduct();
        if (Objects.equals(intent, "N")) {
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
            String intent = readLine();
            Product product = response.inventory().getProduct();
            if (Objects.equals(intent, "Y")) {
                bonusItems.put(product, bonusQuantity);
                return canGetMoreQuantity;
            }
            bonusItems.put(product, bonusQuantity - canGetMoreQuantity);
        }
        return 0;
    }

    private static Map<String, Integer> getStringIntegerMap() {
        String[] split = promptProductNameAndQuantity();
        Map<String, Integer> purchasedItems = new HashMap<>();
        for (String s : split) {
            String[] splitInput = s.split("-");
            String productName = splitInput[0];
            int quantity = Converter.convertToInteger(splitInput[1]);
            purchasedItems.put(productName, quantity);
        }
        return purchasedItems;
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

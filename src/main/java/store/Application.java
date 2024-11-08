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

    public static void main(String[] args) throws IOException {
        printStartMessage();
        Inventories inventories = addInventory();
        Promotions promotions = addPromotion();
        PaymentSystem paymentSystem = new PaymentSystem(inventories, promotions);
        LocalDate now = DateTimes.now().toLocalDate();
        Map<String, Integer> purchasedItems = getStringIntegerMap();
        Map<String, Integer> bonusItems = new HashMap<>();
        for (Entry<String, Integer> entry : purchasedItems.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            Response response = paymentSystem.canBuy(productName, quantity, now);
            int canGetMoreQuantity = canGetMoreQuantity(bonusItems, productName, response);
            if (canGetMoreQuantity > 0) {
                purchasedItems.put(productName, quantity + canGetMoreQuantity);
            }
            int noPromotionQuantity = checkNoPromotion(bonusItems, productName, response);
            if (noPromotionQuantity > 0) {
                purchasedItems.put(productName, quantity - noPromotionQuantity);
            }
        }
    }

    private static String[] promptProductNameAndQuantity() {
        System.out.println(System.lineSeparator() + "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String input = readLine();
        input = input.replace("[", "");
        input = input.replace("]", "");
        String[] split = input.split(",");
        return split;
    }

    private static int checkNoPromotion(final Map<String, Integer> bonusItems, final String productName,
                                        final Response response) {
        if (response.status() == RESPONSE_STATUS.OUT_OF_STOCK) {
            int totalBonusQuantity = response.bonusQuantity();
            bonusItems.put(productName, totalBonusQuantity);
            int noPromotionQuantityOfResponse = response.noPromotionQuantity();
            System.out.printf(System.lineSeparator() + "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)"
                            + System.lineSeparator(),
                    productName, noPromotionQuantityOfResponse);
            String intent = readLine();
            if (Objects.equals(intent, "N")) {
                return noPromotionQuantityOfResponse;
            }
        }
        return 0;
    }

    private static int canGetMoreQuantity(final Map<String, Integer> bonusItems, final String productName,
                                          final Response response) {
        if (response.status() == RESPONSE_STATUS.CAN_GET_BONUS) {
            int bonusQuantity = response.bonusQuantity();
            int canGetMoreQuantity = response.canGetMoreQuantity();
            System.out.printf(System.lineSeparator() + "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)"
                    + System.lineSeparator(), productName, canGetMoreQuantity);
            String intent = readLine();
            if (Objects.equals(intent, "Y")) {
                bonusItems.put(productName, bonusQuantity);
                return canGetMoreQuantity;
            }
            bonusItems.put(productName, bonusQuantity - canGetMoreQuantity);
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
            print(split);
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

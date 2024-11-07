package store;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Application {

    public static final String INVENTORY_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/products.md";
    public static final String PROMOTION_FILENAME = "/Users/mae/Desktop/archive/wooteco/precourse/java-convenience-store-7-Starlight258/src/main/resources/promotions.md";

    public static void main(String[] args) throws IOException {
        printStartMessage();
        List<Promotion> inputPromotions = new ArrayList<>();
        addPromotion(inputPromotions);
        Promotions promotions = new Promotions(inputPromotions);
        List<Inventory> inventories = new ArrayList<>();
        addInventory(inventories, promotions);
    }

    private static void printStartMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.");
        System.out.println();
    }

    private static void addPromotion(final List<Promotion> promotions) throws IOException {
        List<String> inputs = input(PROMOTION_FILENAME);
        for (String input : inputs) {
            if (input.startsWith("name")) {
                continue;
            }
            String[] split = input.split(",", -1);
            Promotion promotion = getPromotion(split);
            promotions.add(promotion);
        }
    }

    private static Promotion getPromotion(final String[] split) {
        int purchaseQuantity = Converter.convertToInteger(split[1]);
        int bonusQuantity = Converter.convertToInteger(split[2]);
        LocalDate startDate = Parser.parseToLocalDate(split[3]);
        LocalDate endDate = Parser.parseToLocalDate(split[4]);
        return new Promotion(split[0], purchaseQuantity, bonusQuantity, startDate, endDate);
    }

    private static void addInventory(final List<Inventory> inventories, final Promotions promotions)
            throws IOException {
        List<String> inputs = input(INVENTORY_FILENAME);
        for (String input : inputs) {
            if (input.startsWith("name")) {
                continue;
            }
            String[] split = input.split(",", -1);
            print(split);
            Product product = new Product(split[0], new BigDecimal(split[1]));
            Inventory inventory = getInventory(promotions, split, product);
            inventories.add(inventory);
        }
    }

    private static Inventory getInventory(final Promotions promotions,
                                          final String[] split, final Product product) {
        Optional<Promotion> optionalPromotion = promotions.find(split[3]);
        return optionalPromotion.map(
                        promotion -> new Inventory(product, Converter.convertToInteger(split[2]), promotion))
                .orElseGet(() -> new Inventory(product, Converter.convertToInteger(split[2]), null));
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

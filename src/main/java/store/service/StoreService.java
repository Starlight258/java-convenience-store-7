package store.service;

import static store.domain.product.stock.StockStatus.NOT_EXIST;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import store.domain.dto.InventoryDto;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.product.stock.ProductStock;
import store.domain.promotion.MembershipCalculator;
import store.domain.promotion.PromotionProcessor;
import store.domain.promotion.PromotionResult;

public class StoreService {

    public List<InventoryDto> processInventory(final Inventory inventory) {
        return inventory.getInventory().entrySet()
                .stream()
                .map(this::processEachProduct)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<InventoryDto> processEachProduct(final Entry<String, ProductStock> entry) {
        String name = entry.getKey();
        ProductStock productStock = entry.getValue();
        Product product = productStock.getProduct();
        return makeDto(name, product, productStock);
    }

    private List<InventoryDto> makeDto(final String name, final Product product, final ProductStock productStock) {
        int promotionQuantity = productStock.getPromotionQuantity();
        int regularQuantity = productStock.getRegularQuantity();
        int price = product.getPrice();
        return makeDtoList(name, product, promotionQuantity, regularQuantity, price);
    }

    private List<InventoryDto> makeDtoList(final String name, final Product product, final int promotionQuantity,
                                                final int regularQuantity, final int price) {
        List<InventoryDto> list = new ArrayList<>();
        if (promotionQuantity != NOT_EXIST.getValue()) {
            InventoryDto inventoryDto = new InventoryDto(name, price, promotionQuantity,
                    product.getPromotion().getName());
            list.add(inventoryDto);
        }
        list.add(new InventoryDto(name, price, regularQuantity, ""));
        return list;
    }

    public PromotionResult processOrder(final int purchaseQuantity, final ProductStock productStock) {
        ProcessingContext processingContext = createProcessingContext(productStock);
        LocalDate now = DateTimes.now().toLocalDate();
        return processingContext.getPromotionProcessor().processOrder(purchaseQuantity, now);
    }

    public PromotionResult processRegularPayment(final ProductStock productStock,
                                                 final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(productStock);
        return processingContext.getPromotionProcessor().processWithRegularPayment(promotionResult);
    }

    public ProcessingContext createProcessingContext(final ProductStock productStock) {
        PromotionProcessor promotionProcessor = new PromotionProcessor(productStock);
        return new ProcessingContext(productStock, promotionProcessor);
    }

    public PromotionResult processOnlyPromotionPayment(final ProductStock productStock,
                                                       final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(productStock);
        return processingContext.getPromotionProcessor().processOnlyPromotionPayment(promotionResult);
    }

    public PromotionResult processBenefitOption(final ProductStock productStock,
                                                final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(productStock);
        return processingContext.getPromotionProcessor().processBenefitOption(promotionResult);
    }

    public PromotionResult processNoBenefitOption(final ProductStock productStock,
                                                  final PromotionResult promotionResult) {
        ProcessingContext processingContext = createProcessingContext(productStock);
        return processingContext.getPromotionProcessor().processNoBenefitOption(promotionResult);
    }

    public int processMembership(final int productPrice, final PromotionResult promotionResult) {
        MembershipCalculator membershipCalculator = new MembershipCalculator();
        return membershipCalculator.calculate(productPrice, promotionResult);
    }

}

package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import store.domain.product.stock.ProductStock;
import store.domain.promotion.MembershipCalculator;
import store.domain.promotion.PromotionProcessor;
import store.domain.promotion.PromotionResult;

public class StoreService {

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

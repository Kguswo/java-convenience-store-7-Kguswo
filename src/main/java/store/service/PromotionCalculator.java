package store.service;

import store.domain.goods.Goods;
import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

public class PromotionCalculator {
    private final Promotion promotion;
    private final Goods goods;
    private final int quantity;

    public PromotionCalculator(Promotion promotion, Goods goods, int quantity) {
        this.promotion = promotion;
        this.goods = goods;
        this.quantity = quantity;
    }

    // 무료로 받을 수 있는 수량 계산
    public int calculateFreeQuantity() {
        if (!isValidPromotion()) {
            return 0;
        }

        switch (promotion.getType()) {
            case BUY_2_GET_1:
                // 프로모션으로 처리 가능한 세트 수 계산
                int possibleSets = quantity / 3;  // 구매량으로 가능한 세트 수
                int maxSetsWithStock = goods.getStock() / 3;  // 재고로 가능한 세트 수
                int maxSetsWithPromoStock = goods.getStock() / 3;  // 프로모션 세트 수 계산 수정

                // 실제 적용 가능한 세트 수 (세 값 중 최소값)
                int actualSets = Math.min(possibleSets,
                        Math.min(maxSetsWithStock, maxSetsWithPromoStock));

                return actualSets;  // 각 세트당 1개씩 무료 증정

            case MD_RECOMMENDATION:
                return (quantity >= 1 && promotion.hasPromotionStock()) ? 1 : 0;

            default:
                return 0;
        }
    }

    // 프로모션으로 인한 할인 금액 계산
    public int calculateDiscount() {
        if (!isValidPromotion()) {
            return 0;
        }

        switch (promotion.getType()) {
            case BUY_2_GET_1:
                // 프로모션으로 처리 가능한 세트 수 계산
                int possibleSets = quantity / 3;
                int maxSetsWithStock = goods.getStock() / 3;
                int maxSetsWithPromoStock = goods.getStock() / 3;

                // 실제 적용 가능한 세트 수
                int actualSets = Math.min(possibleSets,
                        Math.min(maxSetsWithStock, maxSetsWithPromoStock));

                return goods.getPrice() * actualSets;  // 세트당 1개씩 할인

            case MD_RECOMMENDATION:
                if (quantity >= 1 && promotion.hasPromotionStock()) {
                    return goods.getPrice();
                }
                return 0;

            case FLASH_SALE:
                if (quantity >= 1 && promotion.hasPromotionStock()) {
                    int discountQuantity = Math.min(quantity, promotion.getPromotionStock());
                    return (goods.getPrice() * promotion.getType().getDiscountRate() / 100) * discountQuantity;
                }
                return 0;

            default:
                return 0;
        }
    }

    // 프로모션 적용 가능 여부 확인
    public boolean isPromotionApplicable() {
        if (!isValidPromotion()) {
            return false;
        }

        switch (promotion.getType()) {
            case BUY_2_GET_1:
                return quantity >= 3 && promotion.hasPromotionStock();
            case MD_RECOMMENDATION:
                return quantity >= 1 && promotion.hasPromotionStock();
            case FLASH_SALE:
                return quantity >= 1 && promotion.hasPromotionStock();
            default:
                return false;
        }
    }

    // 프로모션 추가 구매 제안이 필요한지 확인
    public boolean canAddPromotionItems() {
        if (!isValidPromotion() || !promotion.hasPromotionStock()) {
            return false;
        }

        switch (promotion.getType()) {
            case MD_RECOMMENDATION:
                return quantity == 1;  // MD추천상품은 1개 구매시 1개 추가 가능
            case BUY_2_GET_1:
                return quantity == 2;  // 2+1은 2개 구매시 1개 추가 제안
            default:
                return false;
        }
    }

    // 프로모션 미적용 수량 확인 필요 여부
    public boolean needsConfirmation() {
        if (!isValidPromotion() || promotion.getType() != PromotionType.BUY_2_GET_1) {
            return false;
        }
        int possiblePromotionSets = quantity / 3;
        int promotionStock = promotion.getPromotionStock();
        int actualPromotionSets = Math.min(possiblePromotionSets, promotionStock);
        int handledByPromotion = actualPromotionSets * 3;
        int remainingQuantity = quantity - handledByPromotion;
        return remainingQuantity > 0;
    }

    // 프로모션 미적용 수량 계산 수정
    public int getRegularQuantity() {
        if (!isValidPromotion()) {
            return quantity;
        }

        switch (promotion.getType()) {
            case BUY_2_GET_1:
                int availableStock = goods.getStock();
                int completeSetsPossible = availableStock / 3;
                int setsNeededForQuantity = quantity / 3;
                int actualSets = Math.min(completeSetsPossible, setsNeededForQuantity);
                int promotionalQuantity = actualSets * 3;
                return quantity - promotionalQuantity;

            default:
                return quantity;
        }
    }

    private boolean isValidPromotion() {
        if (promotion == null) {
            return false;
        }
        if (!promotion.isValid()) {
            return false;
        }
        if (!goods.hasPromotion()) {
            return false;
        }
        if (promotion.getType() != goods.getPromotionType()) {
            return false;
        }
        if (!promotion.hasPromotionStock()) {
            return false;
        }
        return true;
    }
}
package store.service;

import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

public class PromotionService {

    public int calculateFreeQuantity(Promotion promotion, int quantity) {
        if (promotion == null || !promotion.isValid() || !promotion.hasPromotionStock()) {
            return 0;
        }

        if (promotion.getType() == PromotionType.BUY_2_GET_1) {
            int sets = quantity / 2; // 2개 구매당 1개 증정
            return Math.min(sets, promotion.getPromotionStock());
        }

        return 0;
    }

    public int calculateDiscount(Promotion promotion, int price, int quantity) {
        if (promotion == null || !promotion.isValid()) {
            return 0;
        }

        int freeQuantity = calculateFreeQuantity(promotion, quantity);
        return price * freeQuantity;
    }

    public boolean canApplyPromotion(Promotion promotion, int quantity) {
        if (promotion == null || !promotion.isValid()) {
            return false;
        }

        PromotionType type = promotion.getType();
        return quantity >= (type.getRequiredQuantity() + type.getFreeQuantity());
    }
}
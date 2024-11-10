package store.service;

import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

public class PromotionService {

    public int calculateFreeQuantity(Promotion promotion, int quantity) {
        if (promotion == null || !promotion.isValid() || !promotion.hasPromotionStock()) {
            return 0;
        }

        PromotionType type = promotion.getType();
        int sets = quantity / (type.getRequiredQuantity() + type.getFreeQuantity());
        return Math.min(sets * type.getFreeQuantity(), promotion.getPromotionStock());
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
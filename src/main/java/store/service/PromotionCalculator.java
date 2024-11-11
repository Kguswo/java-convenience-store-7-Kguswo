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
                int maxSetsWithPromoStock = promotion.getPromotionStock();  // 프로모션 재고로 가능한 세트 수

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

        int freeQuantity = calculateFreeQuantity();
        switch (promotion.getType()) {
            case BUY_2_GET_1:
                return goods.getPrice() * freeQuantity;  // 무료 증정 수량만큼 할인

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

        // 프로모션 재고로 처리할 수 있는 수량 계산
        int possiblePromotionSets = quantity / 3;  // 총 구매수량으로 만들 수 있는 세트 수
        System.out.println("DEBUG: 가능한 총 세트 수 = " + possiblePromotionSets);

        int promotionStock = promotion.getPromotionStock();  // 현재 프로모션 재고
        System.out.println("DEBUG: 현재 프로모션 재고 = " + promotionStock);

        // 실제로 처리 가능한 세트 수 계산
        int actualPromotionSets = Math.min(possiblePromotionSets, promotionStock);
        System.out.println("DEBUG: 실제 적용 가능한 세트 수 = " + actualPromotionSets);

        int handledByPromotion = actualPromotionSets * 3;  // 프로모션으로 처리되는 수량
        System.out.println("DEBUG: 프로모션으로 처리되는 수량 = " + handledByPromotion);

        int remainingQuantity = quantity - handledByPromotion;
        System.out.println("DEBUG: 프로모션 미적용 수량 = " + remainingQuantity);

        return remainingQuantity > 0;
    }

    // 프로모션 미적용 수량 계산 수정
    public int getRegularQuantity() {
        if (!isValidPromotion()) {
            return quantity;
        }

        switch (promotion.getType()) {
            case BUY_2_GET_1:
                // 프로모션 상품의 재고로 가능한 완전한 세트 수 계산
                int availableStock = goods.getStock();  // 실제 상품의 재고
                int completeSetsPossible = availableStock / 3;  // 완전한 세트로 가능한 수

                // 실제 구매량으로 필요한 세트 수
                int setsNeededForQuantity = quantity / 3;

                // 실제 적용 가능한 세트 수 (둘 중 작은 값)
                int actualSets = Math.min(completeSetsPossible, setsNeededForQuantity);

                // 프로모션으로 처리된 수량
                int promotionalQuantity = actualSets * 3;

                // 남은 수량 계산
                int remainingQuantity = quantity - promotionalQuantity;

                // 디버깅 출력
                System.out.println("DEBUG: 프로모션 재고로 가능한 세트 수 = " + completeSetsPossible);
                System.out.println("DEBUG: 구매량으로 필요한 세트 수 = " + setsNeededForQuantity);
                System.out.println("DEBUG: 실제 적용된 세트 수 = " + actualSets);
                System.out.println("DEBUG: 프로모션으로 처리된 수량 = " + promotionalQuantity);
                System.out.println("DEBUG: 일반 가격으로 처리될 수량 = " + remainingQuantity);

                return remainingQuantity;

            default:
                return quantity;
        }
    }

    private boolean isValidPromotion() {
        if (promotion == null) {
            System.out.println("프로모션이 null입니다");
            return false;
        }
        if (!promotion.isValid()) {
            System.out.println("프로모션이 유효하지 않습니다");
            return false;
        }
        if (!goods.hasPromotion()) {
            System.out.println("상품에 프로모션이 없습니다");
            return false;
        }
        if (promotion.getType() != goods.getPromotionType()) {
            System.out.println("프로모션 타입이 일치하지 않습니다");
            System.out.println("상품 프로모션: " + goods.getPromotionType());
            System.out.println("적용 프로모션: " + promotion.getType());
            return false;
        }
        if (!promotion.hasPromotionStock()) {
            System.out.println("프로모션 재고가 없습니다");
            return false;
        }

        // 모든 조건 통과시 추가 정보 출력
        System.out.println("프로모션 적용 가능:");
        System.out.println("상품: " + goods.getName());
        System.out.println("구매 수량: " + quantity);
        System.out.println("프로모션 재고: " + promotion.getPromotionStock());

        return true;
    }
}
package store.domain.goods;

public enum PromotionType {
    NONE("없음", 0, 0, 0),
    BUY_2_GET_1("탄산2+1", 2, 1, 0),
    MD_RECOMMENDATION("MD추천상품", 1, 1, 0),
    FLASH_SALE("반짝할인", 0, 0, 30);  // 30% 할인

    private final String description;
    private final int requiredQuantity;
    private final int freeQuantity;
    private final int discountRate;

    PromotionType(String description, int requiredQuantity, int freeQuantity, int discountRate) {
        this.description = description;
        this.requiredQuantity = requiredQuantity;
        this.freeQuantity = freeQuantity;
        this.discountRate = discountRate;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public int getDiscountRate() {
        return discountRate;
    }
}
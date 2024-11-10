package store.domain.goods;

public enum PromotionType {
    NONE("없음", 0, 0),
    BUY_1_GET_1("1+1", 1, 1),
    BUY_2_GET_1("2+1", 2, 1),
    MD_RECOMMENDATION("MD추천상품", 1, 1),
    FLASH_SALE("반짝할인", 0, 0);

    private final String description;
    private final int requiredQuantity;
    private final int freeQuantity;

    PromotionType(String description, int requiredQuantity, int freeQuantity) {
        this.description = description;
        this.requiredQuantity = requiredQuantity;
        this.freeQuantity = freeQuantity;
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
}
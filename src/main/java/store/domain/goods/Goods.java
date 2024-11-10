package store.domain.goods;

public class Goods {
    private final String name;
    private final int price;
    private int stock;
    private final PromotionType promotionType;

    public Goods(String name, int price, int stock, PromotionType promotionType) {
        validatePrice(price);
        validateStock(stock);

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.promotionType = promotionType;
    }

    private void validatePrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("[ERROR] 상품 가격은 0보다 작을 수 없습니다.");
        }
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("[ERROR] 재고는 0보다 작을 수 없습니다.");
        }
    }

    public boolean hasStock() {
        return stock > 0;
    }

    public void decreaseStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalStateException("[ERROR] 재고가 부족합니다.");
        }
        stock -= quantity;
    }

    public boolean hasPromotion() {
        return promotionType != PromotionType.NONE;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public PromotionType getPromotionType() {
        return promotionType;
    }
}
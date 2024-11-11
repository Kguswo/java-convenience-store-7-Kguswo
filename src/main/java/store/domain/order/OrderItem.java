package store.domain.order;

import store.domain.goods.Goods;

public class OrderItem {
    private final Goods goods;
    private final int quantity;
    private final int freeQuantity;

    public OrderItem(Goods goods, int quantity, int freeQuantity) {
        validateQuantities(quantity, freeQuantity);
        this.goods = goods;
        this.quantity = quantity;
        this.freeQuantity = freeQuantity;
    }

    private void validateQuantities(int quantity, int freeQuantity) {
        // 구매 수량과 증정 수량이 모두 0인 경우는 허용하지 않음
        if (quantity == 0 && freeQuantity == 0) {
            throw new IllegalArgumentException("[ERROR] 상품 수량은 0보다 커야 합니다.");
        }
        // 구매 수량이나 증정 수량이 음수인 경우는 허용하지 않음
        if (quantity < 0 || freeQuantity < 0) {
            throw new IllegalArgumentException("[ERROR] 상품 수량은 음수일 수 없습니다.");
        }
    }

    public int calculateAmount() {
        return goods.getPrice() * quantity;
    }

    public Goods getGoods() {
        return goods;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }
}
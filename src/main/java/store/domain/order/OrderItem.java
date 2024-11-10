package store.domain.order;

import store.domain.goods.Goods;

public class OrderItem {
    private final Goods goods;
    private final int quantity;
    private final int freeQuantity;

    public OrderItem(Goods goods, int quantity, int freeQuantity) {
        validateQuantity(quantity);
        this.goods = goods;
        this.quantity = quantity;
        this.freeQuantity = freeQuantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("[ERROR] 상품 수량은 0보다 커야 합니다.");
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
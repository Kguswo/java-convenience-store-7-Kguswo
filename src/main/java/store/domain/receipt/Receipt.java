package store.domain.receipt;

import store.domain.order.Order;
import store.domain.order.OrderItem;

import java.util.List;

public class Receipt {
    private final Order order;
    private final int totalAmount;
    private final int promotionDiscount;
    private final int membershipDiscount;
    private final int finalAmount;

    public Receipt(Order order, int promotionDiscount) {
        this.order = order;
        this.totalAmount = order.calculateTotalAmount();
        this.promotionDiscount = promotionDiscount;
        this.membershipDiscount = order.calculateMembershipDiscount();  // 파라미터 제거
        this.finalAmount = totalAmount - promotionDiscount - membershipDiscount;
    }

    // 구매한 상품 목록
    public List<OrderItem> getPurchasedItems() {
        return order.getItems();
    }

    // 증정품 목록
    public List<OrderItem> getFreeItems() {
        // 증정 수량이 1이상인 아이템만 필터링하고, 새로운 OrderItem 생성
        return order.getItems().stream()
                .filter(item -> item.getFreeQuantity() > 0)
                .map(item -> new OrderItem(item.getGoods(), 0, item.getFreeQuantity()))
                .toList();
    }


    public int getTotalAmount() {
        return totalAmount;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalAmount() {
        return finalAmount;
    }
}
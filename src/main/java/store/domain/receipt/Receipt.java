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
        this.membershipDiscount = calculateMembershipDiscount();
        this.finalAmount = calculateFinalAmount();
    }

    private int calculateMembershipDiscount() {
        int discountableAmount = totalAmount - promotionDiscount;
        return order.calculateMembershipDiscount(discountableAmount);
    }

    private int calculateFinalAmount() {
        return totalAmount - promotionDiscount - membershipDiscount;
    }

    public List<OrderItem> getPurchasedItems() {
        return order.getItems();
    }

    public List<OrderItem> getFreeItems() {
        return order.getItems().stream()
                .filter(item -> item.getFreeQuantity() > 0)
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
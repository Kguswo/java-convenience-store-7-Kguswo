package store.domain.order;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final List<OrderItem> items;
    private final boolean useMembership;
    private static final int MAX_MEMBERSHIP_DISCOUNT = 8000;
    private static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;

    public Order(boolean useMembership) {
        this.items = new ArrayList<>();
        this.useMembership = useMembership;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public int calculateTotalAmount() {
        return items.stream()
                .mapToInt(OrderItem::calculateAmount)
                .sum();
    }

    // 프로모션이 적용되지 않는 상품들의 총 금액 계산
    public int calculateNonPromotionalAmount() {
        return items.stream()
                .filter(item -> !item.getGoods().hasPromotion())
                .mapToInt(OrderItem::calculateAmount)
                .sum();
    }

    public int calculateMembershipDiscount() {
        if (!useMembership) {
            return 0;
        }

        // 프로모션이 적용된 상품은 멤버십 할인에서 제외
        int nonPromotionalAmount = items.stream()
                .filter(item -> !item.getGoods().hasPromotion())
                .mapToInt(OrderItem::calculateAmount)
                .sum();

        int calculatedDiscount = (int) (nonPromotionalAmount * MEMBERSHIP_DISCOUNT_RATE);
        return Math.min(calculatedDiscount, MAX_MEMBERSHIP_DISCOUNT);
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public boolean isUseMembership() {
        return useMembership;
    }
}
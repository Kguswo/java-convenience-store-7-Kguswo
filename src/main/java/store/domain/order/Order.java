package store.domain.order;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final List<OrderItem> items;
    private final boolean useMembership;
    private static final int MAX_MEMBERSHIP_DISCOUNT = 8000;

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

    public int calculateMembershipDiscount(int amount) {
        if (!useMembership) {
            return 0;
        }
        int discount = (int) (amount * 0.3);
        return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public boolean isUseMembership() {
        return useMembership;
    }
}
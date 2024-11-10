package store.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.goods.Goods;
import store.domain.goods.PromotionType;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @DisplayName("총 주문 금액을 계산한다")
    @Test
    void calculateTotalAmount() {
        Order order = new Order(false);
        Goods goods1 = new Goods("콜라", 1000, 10, PromotionType.NONE);
        Goods goods2 = new Goods("사이다", 1500, 5, PromotionType.NONE);

        order.addItem(new OrderItem(goods1, 2, 0));
        order.addItem(new OrderItem(goods2, 1, 0));

        int totalAmount = order.calculateTotalAmount();

        assertThat(totalAmount).isEqualTo(3500);
    }

    @DisplayName("멤버십 할인을 계산한다")
    @Test
    void calculateMembershipDiscount() {
        Order order = new Order(true);
        int amount = 30000;

        int discount = order.calculateMembershipDiscount(amount);

        assertThat(discount).isEqualTo(8000); // 최대 할인액 적용
    }

    @DisplayName("멤버십 미사용시 할인이 적용되지 않는다")
    @Test
    void calculateMembershipDiscountWithoutMembership() {
        Order order = new Order(false);
        int amount = 30000;

        int discount = order.calculateMembershipDiscount(amount);

        assertThat(discount).isZero();
    }
}
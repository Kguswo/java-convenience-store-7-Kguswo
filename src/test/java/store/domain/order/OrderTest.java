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
        // given
        Order order = new Order(false);
        Goods goods1 = new Goods("콜라", 1000, 10, PromotionType.NONE);
        Goods goods2 = new Goods("사이다", 1500, 5, PromotionType.NONE);

        // when
        order.addItem(new OrderItem(goods1, 2, 0));
        order.addItem(new OrderItem(goods2, 1, 0));
        int totalAmount = order.calculateTotalAmount();

        // then
        assertThat(totalAmount).isEqualTo(3500);
    }

    @DisplayName("멤버십 할인을 계산한다")
    @Test
    void calculateMembershipDiscount() {
        // given
        Order order = new Order(true);
        Goods goods = new Goods("일반 상품", 30000, 10, PromotionType.NONE);
        order.addItem(new OrderItem(goods, 1, 0));

        // when
        int discount = order.calculateMembershipDiscount();

        // then
        assertThat(discount).isEqualTo(8000); // 최대 할인액 적용
    }

    @DisplayName("멤버십 미사용시 할인이 적용되지 않는다")
    @Test
    void calculateMembershipDiscountWithoutMembership() {
        // given
        Order order = new Order(false);
        Goods goods = new Goods("일반 상품", 30000, 10, PromotionType.NONE);
        order.addItem(new OrderItem(goods, 1, 0));

        // when
        int discount = order.calculateMembershipDiscount();

        // then
        assertThat(discount).isZero();
    }

    @DisplayName("프로모션이 적용된 상품은 멤버십 할인에서 제외된다")
    @Test
    void calculateMembershipDiscountExcludePromotionalItems() {
        // given
        Order order = new Order(true);
        Goods promotionalGoods = new Goods("할인 상품", 10000, 5, PromotionType.FLASH_SALE);
        Goods normalGoods = new Goods("일반 상품", 20000, 5, PromotionType.NONE);

        order.addItem(new OrderItem(promotionalGoods, 1, 0));
        order.addItem(new OrderItem(normalGoods, 1, 0));

        // when
        int discount = order.calculateMembershipDiscount();

        // then
        // 일반 상품(20000원)에 대해서만 30% 할인 적용
        assertThat(discount).isEqualTo(6000);
    }

    @DisplayName("프로모션이 없는 상품들의 총 금액을 계산한다")
    @Test
    void calculateNonPromotionalAmount() {
        // given
        Order order = new Order(false);
        Goods promotionalGoods = new Goods("할인 상품", 10000, 5, PromotionType.FLASH_SALE);
        Goods normalGoods1 = new Goods("일반 상품1", 20000, 5, PromotionType.NONE);
        Goods normalGoods2 = new Goods("일반 상품2", 15000, 5, PromotionType.NONE);

        order.addItem(new OrderItem(promotionalGoods, 1, 0));
        order.addItem(new OrderItem(normalGoods1, 1, 0));
        order.addItem(new OrderItem(normalGoods2, 1, 0));

        // when
        int nonPromotionalAmount = order.calculateNonPromotionalAmount();

        // then
        assertThat(nonPromotionalAmount).isEqualTo(35000); // 20000 + 15000
    }
}
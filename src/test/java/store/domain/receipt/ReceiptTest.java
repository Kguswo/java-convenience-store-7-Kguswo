package store.domain.receipt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.goods.Goods;
import store.domain.goods.PromotionType;
import store.domain.order.Order;
import store.domain.order.OrderItem;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiptTest {

    @DisplayName("영수증에 최종 금액이 정확히 계산된다")
    @Test
    void calculateFinalAmount() {
        Order order = new Order(true);
        Goods goods = new Goods("콜라", 1000, 10, PromotionType.BUY_2_GET_1);
        order.addItem(new OrderItem(goods, 3, 1));

        int promotionDiscount = 1000; // 1개 무료

        Receipt receipt = new Receipt(order, promotionDiscount);

        assertThat(receipt.getTotalAmount()).isEqualTo(3000);
        assertThat(receipt.getPromotionDiscount()).isEqualTo(1000);
        assertThat(receipt.getMembershipDiscount()).isEqualTo(600); // (3000-1000)*0.3
        assertThat(receipt.getFinalAmount()).isEqualTo(1400);
    }

    @DisplayName("증정 상품이 있는 주문의 영수증이 정상적으로 생성된다")
    @Test
    void createReceiptWithFreeItems() {
        Order order = new Order(false);
        Goods goods = new Goods("콜라", 1000, 10, PromotionType.BUY_2_GET_1);
        order.addItem(new OrderItem(goods, 2, 1));

        Receipt receipt = new Receipt(order, 1000);

        assertThat(receipt.getFreeItems()).hasSize(1);
        assertThat(receipt.getFreeItems().get(0).getFreeQuantity()).isEqualTo(1);
    }
}
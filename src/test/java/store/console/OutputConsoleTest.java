package store.console;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.goods.Goods;
import store.domain.goods.PromotionType;
import store.domain.order.Order;
import store.domain.order.OrderItem;
import store.domain.receipt.Receipt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OutputConsoleTest {
    private OutputConsole outputConsole;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputConsole = new OutputConsole();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("2+1 프로모션 상품 영수증 출력 테스트")
    void printReceiptWithBuy2Get1Promotion() {
        // given
        Goods cola = new Goods("콜라", 1000, 10, PromotionType.BUY_2_GET_1);
        OrderItem orderItem = new OrderItem(cola, 3, 1); // 3개 구매, 1개 증정

        // 디버그 출력 추가
        System.out.println("Free Quantity: " + orderItem.getFreeQuantity());

        Order order = new Order(false);
        order.addItem(orderItem);
        Receipt receipt = new Receipt(order, 1000);

        // 디버그 출력 추가
        List<OrderItem> freeItems = receipt.getFreeItems();
        System.out.println("Free Items size: " + freeItems.size());
        freeItems.forEach(item ->
                System.out.println("Free Item: " + item.getGoods().getName() +
                        ", Quantity: " + item.getFreeQuantity()));

        // when
        outputConsole.printReceipt(receipt);
        String output = outputStream.toString();
        System.out.println("Actual output:\n" + output);  // 실제 출력 확인

        // then
        assertThat(output)
                .contains("==============W 편의점================")
                .contains("상품명\t\t수량\t금액")
                .contains("콜라\t\t3\t3,000")
                .contains("=============증    정===============")
                .contains("콜라\t\t1");
    }

    @Test
    @DisplayName("MD추천상품 프로모션 영수증 출력 테스트")
    void printReceiptWithMDRecommendationPromotion() {
        // given
        Goods juice = new Goods("오렌지주스", 1800, 9, PromotionType.MD_RECOMMENDATION);
        OrderItem orderItem = new OrderItem(juice, 1, 1); // 1개 구매, 1개 증정
        Order order = new Order(false); // 멤버십 미사용
        order.addItem(orderItem);
        Receipt receipt = new Receipt(order, 1800); // 1800원 프로모션 할인

        // when
        outputConsole.printReceipt(receipt);
        String output = outputStream.toString();

        // then
        assertThat(output)
                .contains("==============W 편의점================")
                .contains("상품명\t\t수량\t금액")
                .contains("오렌지주스\t1\t1,800")
                .contains("=============증    정===============")
                .contains("오렌지주스\t1")
                .contains("총구매액\t\t1\t1,800")
                .contains("행사할인\t\t\t-1,800");
    }

    @Test
    @DisplayName("증정 상품이 없는 경우 영수증 출력 테스트")
    void printReceiptWithoutPromotion() {
        // given
        Goods water = new Goods("물", 500, 10, PromotionType.NONE);
        OrderItem orderItem = new OrderItem(water, 2, 0); // 2개 구매, 증정 없음
        Order order = new Order(false); // 멤버십 미사용
        order.addItem(orderItem);
        Receipt receipt = new Receipt(order, 0); // 할인 없음

        // when
        outputConsole.printReceipt(receipt);
        String output = outputStream.toString();

        // then
        assertThat(output)
                .contains("==============W 편의점================")
                .contains("상품명\t\t수량\t금액")
                .contains("물\t\t2\t1,000")
                .doesNotContain("=============증    정===============")
                .contains("총구매액\t\t2\t1,000")
                .contains("행사할인\t\t\t-0");
    }
}
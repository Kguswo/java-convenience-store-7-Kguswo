package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.console.InputConsole.OrderInput;
import store.domain.goods.Goods;
import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;
import store.domain.receipt.Receipt;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {
    private OrderService orderService;
    private GoodsService goodsService;
    private PromotionService promotionService;

    @BeforeEach
    void setUp() {
        goodsService = new GoodsService();
        promotionService = new PromotionService();
        orderService = new OrderService(goodsService, promotionService);

        // 테스트용 상품 및 프로모션 설정
        initializeTestData();
    }

    @DisplayName("주문을 생성하고 영수증을 발행한다")
    @Test
    void createOrder() {
        // given
        List<OrderInput> inputs = List.of(
            new OrderInput("콜라", 3)
        );

        // when
        Receipt receipt = orderService.createOrder(inputs, true);

        // then
        assertThat(receipt.getTotalAmount()).isEqualTo(3000);
        assertThat(receipt.getPromotionDiscount()).isEqualTo(1000);
        assertThat(receipt.getMembershipDiscount()).isEqualTo(600);
        assertThat(receipt.getFinalAmount()).isEqualTo(1400);
    }

    @DisplayName("재고보다 많은 수량을 주문하면 예외가 발생한다")
    @Test
    void orderExceedingStock() {
        // given
        List<OrderInput> inputs = List.of(
            new OrderInput("콜라", 11)
        );

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(inputs, false))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("[ERROR]");
    }

    private void initializeTestData() {
        Goods cola = new Goods("콜라", 1000, 10, PromotionType.BUY_2_GET_1);
        Promotion promotion = new Promotion(
            "콜라",
            PromotionType.BUY_2_GET_1,
            5,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1)
        );

        // GoodsService에 테스트 데이터 설정
        try {
            var field = GoodsService.class.getDeclaredField("goodsMap");
            field.setAccessible(true);
            ((java.util.Map<String, Goods>) field.get(goodsService)).put("콜라", cola);

            field = GoodsService.class.getDeclaredField("promotionMap");
            field.setAccessible(true);
            ((java.util.Map<String, Promotion>) field.get(goodsService)).put("콜라", promotion);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
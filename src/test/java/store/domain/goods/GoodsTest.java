package store.domain.goods;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoodsTest {

    @DisplayName("상품을 생성한다")
    @Test
    void createGoods() {
        String name = "콜라";
        int price = 1000;
        int stock = 10;
        PromotionType type = PromotionType.BUY_2_GET_1;

        Goods goods = new Goods(name, price, stock, type);

        assertThat(goods.getName()).isEqualTo(name);
        assertThat(goods.getPrice()).isEqualTo(price);
        assertThat(goods.getStock()).isEqualTo(stock);
        assertThat(goods.getPromotionType()).isEqualTo(type);
    }

    @DisplayName("상품 가격이 0보다 작으면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {-1, -1000})
    void createGoodsWithInvalidPrice(int price) {
        assertThatThrownBy(() -> new Goods("콜라", price, 10, PromotionType.NONE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR]");
    }

    @DisplayName("재고를 감소시킨다")
    @Test
    void decreaseStock() {
        Goods goods = new Goods("콜라", 1000, 10, PromotionType.NONE);

        goods.decreaseStock(3);

        assertThat(goods.getStock()).isEqualTo(7);
    }

    @DisplayName("재고보다 많은 수량을 감소시키면 예외가 발생한다")
    @Test
    void decreaseStockOverAmount() {
        Goods goods = new Goods("콜라", 1000, 10, PromotionType.NONE);

        assertThatThrownBy(() -> goods.decreaseStock(11))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("[ERROR]");
    }
}
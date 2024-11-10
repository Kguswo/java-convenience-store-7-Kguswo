package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.goods.Goods;
import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoodsServiceTest {
    private GoodsService goodsService;
    private static final String TEST_PRODUCTS_PATH = "src/test/resources/test-products.md";
    private static final String TEST_PROMOTIONS_PATH = "src/test/resources/test-promotions.md";

    @BeforeEach
    void setUp() {
        goodsService = new GoodsService();
    }

    @DisplayName("상품 정보를 파일에서 읽어온다")
    @Test
    void initializeGoods() {
        // when
        goodsService.initializeGoods(TEST_PRODUCTS_PATH, TEST_PROMOTIONS_PATH);

        // then
        assertThat(goodsService.getAllGoods()).hasSize(2);

        Goods cola = goodsService.findGoods("콜라");
        assertThat(cola.getPrice()).isEqualTo(1000);
        assertThat(cola.getStock()).isEqualTo(10);
        assertThat(cola.getPromotionType()).isEqualTo(PromotionType.BUY_2_GET_1);
    }

    @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
    @Test
    void findNonExistentGoods() {
        // given
        goodsService.initializeGoods(TEST_PRODUCTS_PATH, TEST_PROMOTIONS_PATH);

        // when & then
        assertThatThrownBy(() -> goodsService.findGoods("없는상품"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR]");
    }

    @DisplayName("프로모션 정보를 조회한다")
    @Test
    void findPromotion() {
        // given
        goodsService.initializeGoods(TEST_PRODUCTS_PATH, TEST_PROMOTIONS_PATH);

        // when
        Promotion promotion = goodsService.findPromotion("콜라");

        // then
        assertThat(promotion).isNotNull();
        assertThat(promotion.getType()).isEqualTo(PromotionType.BUY_2_GET_1);
    }
}
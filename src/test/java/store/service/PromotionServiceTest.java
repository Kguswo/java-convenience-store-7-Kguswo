package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionServiceTest {
    private PromotionService promotionService;
    private Promotion validPromotion;

    @BeforeEach
    void setUp() {
        promotionService = new PromotionService();
        validPromotion = new Promotion(
            "콜라",
            PromotionType.BUY_2_GET_1,
            5,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1)
        );
    }

    @DisplayName("2+1 프로모션의 무료 증정 수량을 계산한다")
    @Test
    void calculateFreeQuantityFor2Plus1() {
        // when
        int freeQuantity = promotionService.calculateFreeQuantity(validPromotion, 6);

        // then
        assertThat(freeQuantity).isEqualTo(2);
    }

    @DisplayName("프로모션 할인 금액을 계산한다")
    @Test
    void calculatePromotionDiscount() {
        // given
        int price = 1000;
        int quantity = 6;

        // when
        int discount = promotionService.calculateDiscount(validPromotion, price, quantity);

        // then
        assertThat(discount).isEqualTo(2000); // 2개 무료
    }

    @DisplayName("프로모션 적용 가능 여부를 확인한다")
    @Test
    void checkPromotionApplicability() {
        // when & then
        assertThat(promotionService.canApplyPromotion(validPromotion, 3)).isTrue();
        assertThat(promotionService.canApplyPromotion(validPromotion, 2)).isFalse();
    }
}
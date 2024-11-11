package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.goods.Goods;
import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionCalculatorTest {
    private Goods promotionalGoods;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        // 테스트용 상품과 프로모션 설정
        promotionalGoods = new Goods("콜라", 1000, 7, PromotionType.BUY_2_GET_1);
        promotion = new Promotion(
                "탄산2+1",
                PromotionType.BUY_2_GET_1,
                7,  // 프로모션 재고
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1)
        );
    }

    @Test
    @DisplayName("2+1 프로모션: 콜라 3개 구매 시 1개 무료")
    void calculateFreeQuantityForThreeItems() {
        // Given
        PromotionCalculator calculator = new PromotionCalculator(promotion, promotionalGoods, 3);

        // When
        int freeQuantity = calculator.calculateFreeQuantity();

        // Then
        assertThat(freeQuantity).isEqualTo(1);
        System.out.println("콜라 3개 구매 시 무료 증정 수량: " + freeQuantity);
    }

    @Test
    @DisplayName("2+1 프로모션: 콜라 10개 구매 시 2개 무료")
    void calculateFreeQuantityForTenItems() {
        // Given
        PromotionCalculator calculator = new PromotionCalculator(promotion, promotionalGoods, 10);

        // When
        int freeQuantity = calculator.calculateFreeQuantity();
        int discount = calculator.calculateDiscount();

        // Then
        assertThat(freeQuantity).isEqualTo(2); // 2+1 프로모션 2번 적용
        assertThat(discount).isEqualTo(2000); // 1000원 × 2개

        System.out.println("=== 콜라 10개 구매 테스트 ===");
        System.out.println("무료 증정 수량: " + freeQuantity);
        System.out.println("할인 금액: " + discount);
        System.out.println("프로모션 재고: " + promotion.getPromotionStock());
        System.out.println("상품 재고: " + promotionalGoods.getStock());
    }

    @Test
    @DisplayName("2+1 프로모션: 프로모션 재고 계산 검증")
    void verifyPromotionStockCalculation() {
        // Given
        PromotionCalculator calculator = new PromotionCalculator(promotion, promotionalGoods, 10);

        // When
        int regularQuantity = calculator.getRegularQuantity();
        int freeQuantity = calculator.calculateFreeQuantity();

        // Then
        System.out.println("=== 프로모션 재고 계산 검증 ===");
        System.out.println("일반 구매 수량: " + regularQuantity);
        System.out.println("무료 증정 수량: " + freeQuantity);

        // 프로모션 적용 가능한 세트 수 계산
        int possibleSets = promotionalGoods.getStock() / 3;
        System.out.println("적용 가능한 프로모션 세트 수: " + possibleSets);

        assertThat(regularQuantity).isEqualTo(4); // 프로모션 미적용 수량
        assertThat(freeQuantity).isEqualTo(2); // 2세트에 대한 무료 증정
    }
}
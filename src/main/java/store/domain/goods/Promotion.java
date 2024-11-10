package store.domain.goods;

import camp.nextstep.edu.missionutils.DateTimes;

import java.time.LocalDate;

public class Promotion {
    private final String goodsName;
    private final PromotionType type;
    private int promotionStock;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(String goodsName, PromotionType type, int promotionStock,
                    LocalDate startDate, LocalDate endDate) {
        this.goodsName = goodsName;
        this.type = type;
        this.promotionStock = promotionStock;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isValid() {
        LocalDate currentDate = DateTimes.now().toLocalDate();
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }

    public boolean hasPromotionStock() {
        return promotionStock > 0;
    }

    public void decreasePromotionStock(int quantity) {
        if (promotionStock < quantity) {
            throw new IllegalStateException("[ERROR] 프로모션 재고가 부족합니다.");
        }
        promotionStock -= quantity;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public PromotionType getType() {
        return type;
    }

    public int getPromotionStock() {
        return promotionStock;
    }
}
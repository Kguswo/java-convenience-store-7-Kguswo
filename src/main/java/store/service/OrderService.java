package store.service;

import store.console.InputConsole.OrderInput;
import store.domain.goods.Goods;
import store.domain.goods.Promotion;
import store.domain.order.Order;
import store.domain.order.OrderItem;
import store.domain.receipt.Receipt;

import java.util.List;

public class OrderService {
    private final GoodsService goodsService;
    private final PromotionService promotionService;

    public OrderService(GoodsService goodsService, PromotionService promotionService) {
        this.goodsService = goodsService;
        this.promotionService = promotionService;
    }

    public Receipt createOrder(List<OrderInput> orderInputs, boolean useMembership) {
        Order order = new Order(useMembership);
        int totalPromotionDiscount = 0;

        for (OrderInput input : orderInputs) {
            Goods goods = goodsService.findGoods(input.getName());
            validateStock(goods, input.getQuantity());

            int freeQuantity = calculateFreeQuantity(goods, input.getQuantity());
            OrderItem item = new OrderItem(goods, input.getQuantity(), freeQuantity);

            order.addItem(item);
            totalPromotionDiscount += calculatePromotionDiscount(goods, input.getQuantity());

            updateStock(goods, input.getQuantity() + freeQuantity);
        }

        return new Receipt(order, totalPromotionDiscount);
    }

    private void validateStock(Goods goods, int quantity) {
        if (goods.getStock() < quantity) {
            throw new IllegalStateException("[ERROR] 재고가 부족합니다.");
        }
    }

    private int calculateFreeQuantity(Goods goods, int quantity) {
        if (!goods.hasPromotion()) {
            return 0;
        }

        Promotion promotion = goodsService.findPromotion(goods.getName());
        return promotionService.calculateFreeQuantity(promotion, quantity);
    }

    private int calculatePromotionDiscount(Goods goods, int quantity) {
        if (!goods.hasPromotion()) {
            return 0;
        }

        Promotion promotion = goodsService.findPromotion(goods.getName());
        return promotionService.calculateDiscount(promotion, goods.getPrice(), quantity);
    }

    private void updateStock(Goods goods, int quantity) {
        goods.decreaseStock(quantity);

        Promotion promotion = goodsService.findPromotion(goods.getName());
        if (promotion != null && promotion.isValid()) {
            promotion.decreasePromotionStock(quantity);
        }
    }
}
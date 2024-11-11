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
        int totalPromotionDiscount = processOrderItems(orderInputs, order);
        return new Receipt(order, totalPromotionDiscount);
    }

    // 프로모션 추가 구매 가능 여부 확인 (MD추천상품 1+1)
    public boolean canAddPromotionItems(Goods goods, int quantity) {
        if (!goods.hasPromotion()) {
            return false;
        }

        PromotionCalculator calculator = createPromotionCalculator(goods, quantity);
        return calculator.canAddPromotionItems();
    }

    // 프로모션 무료 증정 수량 확인
    public int getPromotionFreeQuantity(Goods goods) {
        if (!goods.hasPromotion()) {
            return 0;
        }

        Promotion promotion = goodsService.findPromotion(goods.getPromotionType().getDescription());
        if (promotion == null || !promotion.isValid() || !promotion.hasPromotionStock()) {
            return 0;
        }
        return promotion.getType().getFreeQuantity();
    }

    // 프로모션 미적용 수량 확인 필요 여부 (탄산2+1)
    public boolean needsPromotionConfirmation(Goods goods, int quantity) {
        if (!goods.hasPromotion()) {
            return false;
        }

        PromotionCalculator calculator = createPromotionCalculator(goods, quantity);
        return calculator.needsConfirmation();
    }

    // 프로모션 미적용 수량 확인
    public int getRegularQuantity(Goods goods, int quantity) {
        if (!goods.hasPromotion()) {
            return quantity;
        }

        PromotionCalculator calculator = createPromotionCalculator(goods, quantity);
        Promotion promotion = goodsService.findPromotion(goods.getPromotionType().getDescription());

        if (promotion == null || !promotion.isValid()) {
            return quantity;
        }

        // calculator의 getRegularQuantity() 메서드를 사용하도록 수정
        return calculator.getRegularQuantity();
    }

    private int processOrderItems(List<OrderInput> orderInputs, Order order) {
        return orderInputs.stream()
                .mapToInt(input -> processOrderItem(input, order))
                .sum();
    }

    private int processOrderItem(OrderInput input, Order order) {
        Goods goods = findAndValidateGoods(input.getName());
        validateStock(goods, input.getQuantity());

        PromotionCalculator calculator = createPromotionCalculator(goods, input.getQuantity());
        int freeQuantity = calculator.calculateFreeQuantity();
        int discount = calculator.calculateDiscount();

        OrderItem orderItem = new OrderItem(goods, input.getQuantity(), freeQuantity);
        order.addItem(orderItem);

        updateStocks(goods, input.getQuantity(), freeQuantity);

        return discount;

    }


    private Goods findAndValidateGoods(String name) {
        Goods goods = goodsService.findGoods(name);
        if (goods == null) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        return goods;
    }

    private void validateStock(Goods goods, int quantity) {
        int totalStock = goodsService.getTotalStock(goods.getName());
        if (totalStock < quantity) {
            throw new IllegalStateException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }


    private PromotionCalculator createPromotionCalculator(Goods goods, int quantity) {
        Promotion promotion = goods.hasPromotion() ?
                goodsService.findPromotion(goods.getPromotionType().getDescription()) : null;
        return new PromotionCalculator(promotion, goods, quantity);
    }

    private void createAndAddOrderItem(Order order, Goods goods, int quantity, int freeQuantity) {
        OrderItem item = new OrderItem(goods, quantity, freeQuantity);
        order.addItem(item);
    }

    private void updateStocks(Goods goods, int quantity, int freeQuantity) {
        // 구매 수량만큼만 재고에서 차감
        int totalDeductQuantity = quantity;

        // 프로모션 상품 재고 먼저 사용
        List<Goods> promotionalGoods = goodsService.getAllGoodsWithName(goods.getName())
                .stream()
                .filter(Goods::hasPromotion)
                .filter(Goods::hasStock)
                .toList();

        int remainingQuantity = deductStock(promotionalGoods, totalDeductQuantity);

        // 남은 수량은 일반 재고에서 차감
        if (remainingQuantity > 0) {
            List<Goods> regularGoods = goodsService.getAllGoodsWithName(goods.getName())
                    .stream()
                    .filter(g -> !g.hasPromotion())
                    .filter(Goods::hasStock)
                    .toList();

            deductStock(regularGoods, remainingQuantity);
        }

        // 프로모션 재고 차감 관련 코드 제거 (이미 앞에서 처리됨)
    }

    private int deductStock(List<Goods> goodsList, int quantity) {
        int remainingQuantity = quantity;
        for (Goods goods : goodsList) {
            int deductQuantity = Math.min(remainingQuantity, goods.getStock());
            if (deductQuantity > 0) {
                goods.decreaseStock(deductQuantity);
                remainingQuantity -= deductQuantity;
            }
            if (remainingQuantity == 0) break;
        }
        return remainingQuantity;
    }
}
package store;

import store.console.InputConsole;
import store.console.OutputConsole;
import store.domain.receipt.Receipt;
import store.service.GoodsService;
import store.service.OrderService;
import store.service.PromotionService;

import java.util.ArrayList;
import java.util.List;

public class Application {
    private static final String PRODUCTS_PATH = "src/main/resources/products.md";
    private static final String PROMOTIONS_PATH = "src/main/resources/promotions.md";

    private final InputConsole inputConsole;
    private final OutputConsole outputConsole;
    private final GoodsService goodsService;
    private final OrderService orderService;

    public Application() {
        this.inputConsole = new InputConsole();
        this.outputConsole = new OutputConsole();
        this.goodsService = new GoodsService();
        this.orderService = new OrderService(goodsService, new PromotionService());
    }

    public void run() {
        initializeStore();

        do {
            try {
                processOrder();
            } catch (IllegalArgumentException | IllegalStateException e) {
                outputConsole.printErrorMessage(e.getMessage());
                continue;
            }
        } while (askForAdditionalPurchase());
    }

    private void initializeStore() {
        try {
            goodsService.initializeGoods(PRODUCTS_PATH, PROMOTIONS_PATH);
            outputConsole.printWelcomeMessage();
            outputConsole.printGoods(goodsService.getAllGoods());
        } catch (IllegalStateException e) {
            outputConsole.printErrorMessage(e.getMessage());
            throw e;
        }
    }

    private void processOrder() {
        List<InputConsole.OrderInput> finalOrderInputs = new ArrayList<>();
        var inputs = inputConsole.readOrder();
        System.out.println();  // 입력 후 빈 줄 추가

        // 먼저 모든 주문에 대해 재고 검증
        for (var input : inputs) {
            var goods = goodsService.findGoods(input.getName());
            // 재고 검증
            orderService.validateStock(goods, input.getQuantity());
        }


        for (var input : inputs) {
            var modifiedInput = input;
            var goods = goodsService.findGoods(input.getName());

            // 프로모션 추가 구매 가능 여부 확인
            if (orderService.canAddPromotionItems(goods, input.getQuantity())) {
                int freeQuantity = orderService.getPromotionFreeQuantity(goods);
                String message = String.format(
                        "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까?",
                        goods.getName(), freeQuantity);

                if (inputConsole.readYesNo(message)) {
                    modifiedInput = new InputConsole.OrderInput(
                            input.getName(),
                            input.getQuantity() + freeQuantity
                    );
                }
                System.out.println();  // 프로모션 메시지 후 빈 줄 추가
            }

            // 프로모션 재고 부족 확인
            if (orderService.needsPromotionConfirmation(goods, modifiedInput.getQuantity())) {
                int regularQuantity = orderService.getRegularQuantity(goods, modifiedInput.getQuantity());
                String message = String.format(
                        "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까?",
                        goods.getName(), regularQuantity);

                if (!inputConsole.readYesNo(message)) {
                    throw new IllegalStateException("[ERROR] 구매가 취소되었습니다.");
                }
                System.out.println();  // 확인 메시지 후 빈 줄 추가
            }

            finalOrderInputs.add(modifiedInput);
        }

        boolean useMembership = inputConsole.readYesNo("멤버십 할인을 받으시겠습니까?");
        System.out.println();  // 멤버십 입력 후 빈 줄 추가

        Receipt receipt = orderService.createOrder(finalOrderInputs, useMembership);
        outputConsole.printReceipt(receipt);
    }

    private boolean askForAdditionalPurchase() {
        boolean hasMore = inputConsole.readYesNo(
                "감사합니다. 구매하고 싶은 다른 상품이 있나요?");

        if (hasMore) {
            outputConsole.printWelcomeMessage();
            outputConsole.printGoods(goodsService.getAllGoods());
        }

        return hasMore;
    }

    public static void main(String[] args) {
        new Application().run();
    }
}
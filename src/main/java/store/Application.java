package store;

import store.console.InputConsole;
import store.console.OutputConsole;
import store.domain.receipt.Receipt;
import store.service.GoodsService;
import store.service.OrderService;
import store.service.PromotionService;

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
        var orderInputs = inputConsole.readOrder();

        boolean useMembership = inputConsole.readYesNo(
            "멤버십 할인을 받으시겠습니까?");

        Receipt receipt = orderService.createOrder(orderInputs, useMembership);
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
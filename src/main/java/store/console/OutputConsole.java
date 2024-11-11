package store.console;

import store.domain.goods.Goods;
import store.domain.order.OrderItem;
import store.domain.receipt.Receipt;

import java.util.List;

public class OutputConsole {
    private static final String STORE_NAME = "W 편의점";
    private static final String SEPARATOR = "====================================";
    private static final String STORE_HEADER = "==============" + STORE_NAME + "================";
    private static final String GIFT_HEADER = "=============증    정===============";

    public void printWelcomeMessage() {
        System.out.println("안녕하세요. " + STORE_NAME + "입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");
    }

    public void printGoods(List<Goods> goodsList) {
        goodsList.forEach(goods -> {
            String stockInfo = goods.hasStock() ?
                    goods.getStock() + "개" : "재고 없음";
            String promotionInfo = goods.hasPromotion() ?
                    " " + goods.getPromotionType().getDescription() : "";

            System.out.printf("- %s %s원 %s%s%n",
                    goods.getName(),
                    formatNumber(goods.getPrice()),
                    stockInfo,
                    promotionInfo);
        });
        System.out.println();
    }

    public void printReceipt(Receipt receipt) {
        System.out.println(STORE_HEADER);
        System.out.println("상품명\t\t수량\t금액");

        // 구매 항목 출력
        for (OrderItem item : receipt.getPurchasedItems()) {
            String name = item.getGoods().getName();
            String tabs = name.length() <= 3 ? "\t\t" : "\t";
            System.out.printf("%s%s%d\t%,d%n",
                    name,
                    tabs,
                    item.getQuantity(),
                    item.calculateAmount());
        }

        // 증정 품목 출력 - 증정 품목이 있을 때만
        List<OrderItem> freeItems = receipt.getFreeItems();
        if (!freeItems.isEmpty()) {
            System.out.println(GIFT_HEADER);
            for (OrderItem item : freeItems) {
                String name = item.getGoods().getName();
                String tabs = name.length() <= 3 ? "\t\t" : "\t";
                System.out.printf("%s%s%d%n",
                        name,
                        tabs,
                        item.getFreeQuantity());
            }
        }

        System.out.println(SEPARATOR);

        int totalQuantity = receipt.getPurchasedItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        System.out.printf("총구매액\t\t%d\t%,d%n",
                totalQuantity, receipt.getTotalAmount());
        System.out.printf("행사할인\t\t\t-%,d%n",
                receipt.getPromotionDiscount());
        System.out.printf("멤버십할인\t\t\t-%,d%n",
                receipt.getMembershipDiscount());
        System.out.printf("내실돈\t\t\t%,d%n",
                receipt.getFinalAmount());
        System.out.println();
    }

    public void printErrorMessage(String message) {
        System.out.println(message);
    }

    private String formatNumber(int number) {
        return String.format("%,d", number);
    }
}
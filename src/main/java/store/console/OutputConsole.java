package store.console;

import store.domain.goods.Goods;
import store.domain.order.OrderItem;
import store.domain.receipt.Receipt;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OutputConsole {
    private static final String STORE_NAME = "W 편의점";
    private static final String SEPARATOR = "====================================";
    private static final String STORE_HEADER = "==============" + STORE_NAME + "================";
    private static final String GIFT_HEADER = "=============증\t정===============";

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
        printReceiptHeader();
        printPurchasedItems(receipt.getPurchasedItems());
        printGiftItems(receipt.getFreeItems());
        printSeparator();
        printAmountDetails(receipt);
    }

    private void printReceiptHeader() {
        System.out.println(STORE_HEADER);
        System.out.println("상품명\t\t수량\t금액");
    }

    private void printPurchasedItems(List<OrderItem> items) {
        items.forEach(item -> System.out.printf("%s\t\t%d\t%s%n",
            item.getGoods().getName(),
            item.getQuantity(),
            formatNumber(item.calculateAmount())));
    }

    private void printGiftItems(List<OrderItem> freeItems) {
        if (freeItems.isEmpty()) {
            return;
        }
        System.out.println(GIFT_HEADER);
        freeItems.forEach(item -> System.out.printf("%s\t\t%d%n",
            item.getGoods().getName(),
            item.getFreeQuantity()));
    }

    private void printAmountDetails(Receipt receipt) {
        System.out.printf("총구매액\t\t\t%s%n", formatNumber(receipt.getTotalAmount()));
        System.out.printf("행사할인\t\t\t-%s%n", formatNumber(receipt.getPromotionDiscount()));
        System.out.printf("멤버십할인\t\t\t-%s%n", formatNumber(receipt.getMembershipDiscount()));
        System.out.printf("내실돈\t\t\t%s%n", formatNumber(receipt.getFinalAmount()));
    }

    private void printSeparator() {
        System.out.println(SEPARATOR);
    }

    private String formatNumber(int number) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(number);
    }

    public void printErrorMessage(String message) {
        System.out.println(message);
    }
}
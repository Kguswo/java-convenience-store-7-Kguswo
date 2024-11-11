package store.console;

import store.domain.goods.Goods;
import store.domain.order.OrderItem;
import store.domain.receipt.Receipt;

import java.util.List;

public class OutputConsole {
    private static final String STORE_NAME = "W편의점";
    // 44개의 = 문자
    private static final String SEPARATOR = "====================================";
    private static final String STORE_HEADER = "==============W 편의점================";
    private static final String GIFT_HEADER = "=============증     정===============";
    private static final int HEADER_LENGTH = 44;  // 전체 헤더 길이

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

            System.out.printf("%-5s %s원 %s%s%n",
                    goods.getName(),
                    formatNumber(goods.getPrice()),
                    stockInfo,
                    promotionInfo);
        });
        System.out.println();
    }

    public void printReceipt(Receipt receipt) {
        System.out.println(STORE_HEADER);
        System.out.printf("상품명\t\t\t\t수량\t\t금액\n");

        // 구매 항목 출력
        for (OrderItem item : receipt.getPurchasedItems()) {
            System.out.printf("%-5s\t\t\t\t%d\t\t%,d%n",
                    item.getGoods().getName(),
                    item.getQuantity(),
                    item.calculateAmount());
        }

        // 증정 품목 출력
        List<OrderItem> freeItems = receipt.getFreeItems();
        if (!freeItems.isEmpty()) {
            System.out.println(GIFT_HEADER);
            for (OrderItem item : freeItems) {
                System.out.printf("%-5s\t\t\t\t %d%n",
                        item.getGoods().getName(),
                        item.getFreeQuantity());
            }
        }

        System.out.println(SEPARATOR);

        // 총 구매 수량 계산
        int totalQuantity = receipt.getPurchasedItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // 금액 정보 출력
        System.out.printf("총구매액\t\t\t\t%d\t\t%,d%n",
                totalQuantity,
                receipt.getTotalAmount());
        System.out.printf("행사할인\t\t\t\t\t\t-%,d%n",
                receipt.getPromotionDiscount());
        System.out.printf("멤버십할인\t\t\t\t\t\t-%,d%n",
                receipt.getMembershipDiscount());
        System.out.printf("내실돈\t\t\t\t\t\t %,-5d%n",
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
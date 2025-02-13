package store.console;

import camp.nextstep.edu.missionutils.Console;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class InputConsole {
    private static final String ORDER_PATTERN = "^\\[(.*?)\\](,\\[(.*?)\\])*$";
    private static final String ITEM_PATTERN = "^(.*?)-(\\d+)$";
    private static final int MAX_ORDERS = 10; // 한 번에 주문 가능한 최대 상품 종류

    public List<OrderInput> readOrder() {
        while (true) {
            try {
                System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
                String input = Console.readLine();
                validateOrderFormat(input);
                return parseOrderInput(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean readYesNo(String message) {
        while (true) {
            try {
                System.out.println(message + " (Y/N)");
                String input = Console.readLine().toUpperCase();
                validateYesNo(input);
                return "Y".equals(input);
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
            }
        }
    }

    private void validateOrderFormat(String input) {
        if (!Pattern.matches(ORDER_PATTERN, input)) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private void validateYesNo(String input) {
        if (!input.equals("Y") && !input.equals("N")) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
    }

    private List<OrderInput> parseOrderInput(String input) {
        String[] items = input.split(",");
        Set<String> uniqueNames = new HashSet<>();
        return Arrays.stream(items)
                .map(this::parseOrderItem)
                .filter(orderInput -> {
                    if (!uniqueNames.add(orderInput.getName())) {
                        throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
                    }
                    return true;
                })
                .toList();
    }

    private OrderInput parseOrderItem(String item) {
        String content = item.replaceAll("[\\[\\]]", "");
        if (!Pattern.matches(ITEM_PATTERN, content)) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        String[] parts = content.split("-");
        String name = parts[0];
        int quantity;
        try {
            quantity = Integer.parseInt(parts[1]);
            if (quantity <= 0) {
                throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        return new OrderInput(name, quantity);
    }

    public static class OrderInput {
        private final String name;
        private final int quantity;

        public OrderInput(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
package store.service;

import store.domain.goods.Goods;
import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsService {
    private final Map<String, Goods> goodsMap = new HashMap<>();
    private final Map<String, Promotion> promotionMap = new HashMap<>();

    public void initializeGoods(String productsPath, String promotionsPath) {
        loadProducts(productsPath);
        loadPromotions(promotionsPath);
    }

    private void loadProducts(String path) {
        try {
            List<String> lines = Files.readAllLines(Path.of(path));
            for (String line : lines) {
                parseLine(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("[ERROR] 상품 파일을 읽을 수 없습니다.");
        }
    }

    private void loadPromotions(String path) {
        try {
            List<String> lines = Files.readAllLines(Path.of(path));
            for (String line : lines) {
                parsePromotionLine(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("[ERROR] 프로모션 파일을 읽을 수 없습니다.");
        }
    }

    private void parseLine(String line) {
        String[] parts = line.split(",");
        String name = parts[0];
        int price = Integer.parseInt(parts[1]);
        int stock = Integer.parseInt(parts[2]);
        PromotionType type = (parts.length > 3 && parts[3] != null && !parts[3].equals("null")) ?
            parsePromotionType(parts[3]) : PromotionType.NONE;

        // 재고가 없는 버전의 상품도 추가
        if (type != PromotionType.NONE) {
            // 프로모션 상품 추가
            goodsMap.put(name + "-promotion", new Goods(name, price, stock, type));
            // 재고 없는 일반 상품 추가
            goodsMap.put(name + "-regular", new Goods(name, price, 0, PromotionType.NONE));
        } else {
            // 일반 상품 추가
            goodsMap.put(name, new Goods(name, price, stock, PromotionType.NONE));
        }
    }

    private PromotionType parsePromotionType(String type) {
        return switch (type.trim()) {
            case "탄산2+1" -> PromotionType.BUY_2_GET_1;
            case "MD추천상품" -> PromotionType.MD_RECOMMENDATION;
            case "반짝할인" -> PromotionType.FLASH_SALE;
            default -> PromotionType.NONE;
        };
    }

    private void parsePromotionLine(String line) {
        String[] parts = line.split(",");
        // 프로모션 파일 형식: 탄산2+1,2,1,2024-01-01,2024-12-31
        String promotionName = parts[0];
        PromotionType type = parsePromotionType(promotionName);
        int stock = Integer.parseInt(parts[1]);
        LocalDate startDate = LocalDate.parse(parts[3]);
        LocalDate endDate = LocalDate.parse(parts[4]);

        promotionMap.put(promotionName, new Promotion(promotionName, type, stock, startDate, endDate));
    }

    public Goods findGoods(String name) {
        if (!goodsMap.containsKey(name)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다.");
        }
        return goodsMap.get(name);
    }

    public Promotion findPromotion(String name) {
        return promotionMap.get(name);
    }

    public List<Goods> getAllGoods() {
        return goodsMap.values().stream()
            .sorted((a, b) -> {
                if (a.getName().equals(b.getName())) {
                    if (a.hasPromotion() && !b.hasPromotion()) return -1;
                    if (!a.hasPromotion() && b.hasPromotion()) return 1;
                }
                return 0;
            })
            .toList();
    }
}
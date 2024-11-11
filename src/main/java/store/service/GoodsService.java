package store.service;

import store.domain.goods.Goods;
import store.domain.goods.Promotion;
import store.domain.goods.PromotionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GoodsService {
    private final Map<String, List<Goods>> goodsMap = new LinkedHashMap<>();
    private final Map<String, Promotion> promotionMap = new LinkedHashMap<>();

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
        PromotionType type = (parts.length > 3 && !parts[3].equals("null")) ?
                parsePromotionType(parts[3]) : PromotionType.NONE;

        Goods goods = new Goods(name, price, stock, type);
        goodsMap.computeIfAbsent(name, k -> new ArrayList<>()).add(goods);
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
        String promotionName = parts[0];
        PromotionType type = parsePromotionType(promotionName);
        int stock = Integer.parseInt(parts[1]);
        LocalDate startDate = LocalDate.parse(parts[3]);
        LocalDate endDate = LocalDate.parse(parts[4]);

        promotionMap.put(promotionName, new Promotion(promotionName, type, stock, startDate, endDate));
    }

    public Goods findGoods(String name) {
        List<Goods> goodsList = goodsMap.get(name);
        if (goodsList == null) {
            return null;
        }

        // 1. 프로모션 상품 중 재고가 있는 것을 우선 반환
        for (Goods goods : goodsList) {
            if (goods.hasPromotion() && goods.hasStock()) {
                return goods;
            }
        }

        // 2. 일반 상품 중 재고가 있는 것을 반환
        for (Goods goods : goodsList) {
            if (!goods.hasPromotion() && goods.hasStock()) {
                return goods;
            }
        }

        // 3. 재고가 없는 경우 첫 번째 상품 반환
        return goodsList.get(0);
    }


    public Promotion findPromotion(String name) {
        return promotionMap.get(name);
    }

    public List<Goods> getAllGoods() {
        List<Goods> allGoods = new ArrayList<>();
        for (List<Goods> goodsList : goodsMap.values()) {
            allGoods.addAll(goodsList);
        }
        return allGoods;
    }

    public int getTotalStock(String name) {
        List<Goods> goodsList = goodsMap.get(name);
        if (goodsList == null) {
            return 0;
        }

        return goodsList.stream()
                .mapToInt(Goods::getStock)
                .sum();
    }

    public List<Goods> getAllGoodsWithName(String name) {
        List<Goods> goodsList = goodsMap.get(name);
        return goodsList != null ? new ArrayList<>(goodsList) : new ArrayList<>();
    }
}
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
        // 파일 형식에 맞게 파싱 로직 구현
        // 예: 콜라,1000,10,탄산2+1
        String[] parts = line.split(",");
        String name = parts[0];
        int price = Integer.parseInt(parts[1]);
        int stock = Integer.parseInt(parts[2]);
        PromotionType type = parts.length > 3 ?
            PromotionType.valueOf(parts[3]) : PromotionType.NONE;

        goodsMap.put(name, new Goods(name, price, stock, type));
    }

    private void parsePromotionLine(String line) {
        // 프로모션 파일 파싱 로직 구현
        String[] parts = line.split(",");
        String name = parts[0];
        PromotionType type = PromotionType.valueOf(parts[1]);
        int stock = Integer.parseInt(parts[2]);
        LocalDate startDate = LocalDate.parse(parts[3]);
        LocalDate endDate = LocalDate.parse(parts[4]);

        promotionMap.put(name, new Promotion(name, type, stock, startDate, endDate));
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
        return new ArrayList<>(goodsMap.values());
    }
}
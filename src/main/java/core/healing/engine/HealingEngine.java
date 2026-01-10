package core.healing.engine;

import core.healing.HealingConfig;
import core.healing.IHealingDriver;
import core.healing.model.ElementNode;
import core.healing.model.HealingResult;
import core.healing.strategy.*;
import core.platform.utils.Logger;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

/**
 * Core healing engine that orchestrates the multi-strategy healing process.
 */
public class HealingEngine {

    private final List<HealingStrategy> strategies;

    public HealingEngine() {
        strategies = new ArrayList<>();
        List<String> configStrategies = HealingConfig.getInstance().getStrategies();

        if (configStrategies != null && !configStrategies.isEmpty()) {
            for (String strategyName : configStrategies) {
                HealingStrategy strategy = createStrategy(strategyName);
                if (strategy != null) {
                    strategies.add(strategy);
                }
            }
        } else {
            // Fallback defaults
            strategies.add(new ExactAttributeStrategy());
            strategies.add(new KeyBasedStrategy());
            strategies.add(new TextBasedStrategy());
            strategies.add(new CrossAttributeStrategy());
            strategies.add(new SemanticValueStrategy());
            strategies.add(new StructuralStrategy());
            strategies.add(new NeighborStrategy());
            strategies.add(new LocationHealingStrategy());
            strategies.add(new RagHealingStrategy());
        }

        Logger.info("Healing Engine initialized with %d strategies", strategies.size());
    }

    private HealingStrategy createStrategy(String name) {
        switch (name) {
            case "ExactAttributeStrategy":
                return new ExactAttributeStrategy();
            case "KeyBasedStrategy":
                return new KeyBasedStrategy();
            case "TextBasedStrategy":
                return new TextBasedStrategy();
            case "CrossAttributeStrategy":
                return new CrossAttributeStrategy();
            case "SemanticValueStrategy":
                return new SemanticValueStrategy();
            case "StructuralStrategy":
                return new StructuralStrategy();
            case "NeighborStrategy":
                return new NeighborStrategy();
            case "LocationHealingStrategy":
                return new LocationHealingStrategy();
            case "RagHealingStrategy":
                return new RagHealingStrategy();
            case "VisualHealingStrategy":
                return new VisualHealingStrategy();
            default:
                Logger.warn("Unknown strategy in config: %s", name);
                return null;
        }
    }

    public HealingResult heal(IHealingDriver driver, ElementNode original) {
        long startTime = System.currentTimeMillis();

        // Find candidates
        List<ElementNode> candidates = CandidateFinder.findCandidates(driver);

        // Limit candidates if too many (though we already filtered in JS, this is extra
        // safety)
        if (candidates.size() > HealingConfig.MAX_CANDIDATES) {
            candidates = candidates.subList(0, HealingConfig.MAX_CANDIDATES);
        }

        Logger.debug("Found %d candidates for healing", candidates.size());

        ElementNode bestElement = null;
        double bestWeightedScore = 0.0;
        String bestStrategy = "None";

        for (ElementNode candidate : candidates) {
            for (HealingStrategy strategy : strategies) {
                try {
                    double rawScore = strategy.score(original, candidate);
                    /***
                     * Trọng số của chiến lược đã được loại bỏ để đánh giá công bằng hơn giữa các chiến lược.
                     * Nếu cần, có thể thêm lại trọng số sau này.
                     * Mục đích của trong số là chọn ra chiến lược nào ưu tiên nhưng nó cũng làm ảnh hướng của chiến lược đó với mức HEALING_THRESHOLD
                     * vd như so sanh bằng hình là 0.9 * với trong số 0.3 thì lúc nào cũng dưới HEALING_THRESHOLD
                     *
                     * Đầu tư tiên, chúng ta cần đánh giá hiệu quả của từng chiến lược một cách riêng biệt trước khi áp dụng trọng số, có vượt qua HEALING_THRESHOLD
                     * Sau đó những chiến lược nào đã vượt qua HEALING_THRESHOLD rồi thì dùng trọng số để xem nên chọn cái nào nhất
                     */

                    //double weightedScore = rawScore * strategy.getWeight();
                    double weightedScore = rawScore;

                    if (weightedScore > bestWeightedScore) {
                        bestWeightedScore = weightedScore;
                        bestElement = candidate;
                        bestStrategy = strategy.getName();
                    }
                } catch (Exception e) {
                    Logger.error("Error scoring candidate with strategy %s: %s", strategy.getName(), e.getMessage());
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        Logger.info("Healing analysis completed in %dms", duration);

        if (bestWeightedScore >= HealingConfig.HEALING_THRESHOLD && bestElement != null) {
            // Reconstruct locator for the best element
            // We need to build a robust locator for the found element to return it
            bestElement.setLocator(constructLocator(bestElement));

            Logger.info("Healing SUCCESS. Score: %.2f, Strategy: %s, Locator: %s",
                    bestWeightedScore, bestStrategy, bestElement.getLocator());

            return new HealingResult(bestElement, bestWeightedScore, bestStrategy);
        }

        Logger.warn("Healing FAILED. Best score: %.2f (Threshold: %.2f)", bestWeightedScore,
                HealingConfig.HEALING_THRESHOLD);
        return HealingResult.failure();
    }

    private String constructLocator(ElementNode node) {
        // Try id
        String id = node.getAttribute("id");
        if (id != null && !id.isEmpty())
            return "//*[@id='" + id + "']";

        // Try name
        String name = node.getAttribute("name");
        if (name != null && !name.isEmpty())
            return "//*[@name='" + name + "']";

        // Try text
        if (node.getText() != null && !node.getText().isEmpty()) {
            // Simplified text locator
            String text = node.getText().length() > 20 ? node.getText().substring(0, 20) : node.getText();
            return "//" + node.getTagName() + "[contains(text(), '" + text + "')]";
        }

        // Try Neighbor Context (if available and strong)
        if (node.getPrevSiblingText() != null && !node.getPrevSiblingText().isEmpty()
                && node.getPrevSiblingTag() != null) {
            // Heuristic: If we are here, ID/Name/Text strategies likely failed.
            // Construct: //prevTag[contains(text(),'prevText')]/following-sibling::tag[1]
            String prevText = node.getPrevSiblingText().length() > 20 ? node.getPrevSiblingText().substring(0, 20)
                    : node.getPrevSiblingText();
            // Escape single quotes just in case
            prevText = prevText.replace("'", "\'");

            return "//" + node.getPrevSiblingTag() + "[contains(text(), '" + prevText + "')]/following-sibling::"
                    + node.getTagName() + "[1]";
        }

        // Fallback to internal xpath if we had one, or attributes
        for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            if (!entry.getKey().equals("class") && !entry.getKey().equals("style")) {
                return "//" + node.getTagName() + "[@" + entry.getKey() + "='" + entry.getValue() + "']";
            }
        }

        return "//" + node.getTagName(); // Weak fallback
    }
}

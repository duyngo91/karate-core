package core.healing.engine;

import core.healing.HealingConfig;
import core.healing.IHealingDriver;
import core.healing.model.ElementNode;
import core.healing.model.ElementScore;
import core.healing.model.HealingResult;
import core.healing.model.StrategyMatch;
import core.healing.strategy.*;
import core.platform.utils.Logger;

import java.util.*;

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

        List<ElementNode> candidates = CandidateFinder.findCandidates(driver);
        if (candidates.size() > HealingConfig.MAX_CANDIDATES) {
            candidates = candidates.subList(0, HealingConfig.MAX_CANDIDATES);
        }

        Map<ElementNode, ElementScore> scoreMap = new HashMap<>();

        // ===== PHASE 1: NON-VISUAL STRATEGIES =====
        for (ElementNode candidate : candidates) {
            ElementScore elementScore = new ElementScore(candidate);

            for (HealingStrategy strategy : strategies) {
                if (strategy instanceof VisualHealingStrategy) continue;

                double raw = strategy.score(original, candidate);
                double weighted = raw * strategy.getWeight();

                StrategyMatch match = new StrategyMatch(
                        candidate, strategy, raw, weighted
                );
                elementScore.matches.add(match);

                // ✔ PASS chỉ là signal phụ
                if (raw >= HealingConfig.HEALING_THRESHOLD) {
                    elementScore.passCount++;
                }

                // ✔ LUÔN cộng weighted score
                elementScore.totalWeightedScore += weighted;

                // ✔ Track cú đánh mạnh nhất
                elementScore.bestRawScore =
                        Math.max(elementScore.bestRawScore, raw);
            }

            scoreMap.put(candidate, elementScore);
        }

        // ===== PHASE 2: CHOOSE BEST ELEMENT (EVIDENCE-BASED) =====
        List<ElementScore> sorted = scoreMap.values()
                .stream()
                .sorted(Comparator
                        .comparingDouble((ElementScore e) -> e.totalWeightedScore).reversed()
                        .thenComparingDouble(e -> e.bestRawScore).reversed()
                        .thenComparingInt(e -> e.passCount).reversed()
                )
                .toList();

        if (!sorted.isEmpty()) {
            ElementScore best = sorted.get(0);

            // Optional safety check
            if (best.bestRawScore >= HealingConfig.HEALING_THRESHOLD) {
                return best.getHealingResult();
            }
        }

        // ===== PHASE 3: VISUAL FALLBACK (CHOOSE BEST, NOT FIRST) =====
        Logger.warn("DOM healing failed. Trying visual healing...");

        ElementNode bestVisual = null;
        double bestVisualScore = 0;
        HealingStrategy bestVisualStrategy = null;

        for (HealingStrategy strategy : strategies) {
            if (!(strategy instanceof VisualHealingStrategy)) continue;

            for (ElementNode candidate : candidates) {
                double raw = strategy.score(original, candidate);
                if (raw > bestVisualScore) {
                    bestVisualScore = raw;
                    bestVisual = candidate;
                    bestVisualStrategy = strategy;
                }
            }
        }

        if (bestVisual != null && bestVisualScore >= HealingConfig.VISUAL_THRESHOLD) {
            bestVisual.setLocator(constructLocator(bestVisual));
            return new HealingResult(
                    bestVisual,
                    bestVisualScore,
                    bestVisualStrategy.getName()
            );
        }

        // ===== PHASE 4: LAST RESORT (BEST DOM GUESS) =====
        sorted.forEach(System.out::println);

        return !sorted.isEmpty()
                ? sorted.get(0).getHealingResult()
                : HealingResult.failure();
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

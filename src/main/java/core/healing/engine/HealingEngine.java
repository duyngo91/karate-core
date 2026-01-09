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
        strategies.add(new ExactAttributeStrategy());
        strategies.add(new KeyBasedStrategy());
        strategies.add(new TextBasedStrategy());
        strategies.add(new CrossAttributeStrategy());
        strategies.add(new SemanticValueStrategy());
        strategies.add(new StructuralStrategy());

        Logger.info("Healing Engine initialized with %d strategies", strategies.size());
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
                    double weightedScore = rawScore * strategy.getWeight();

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

        // Fallback to internal xpath if we had one, or attributes
        for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            if (!entry.getKey().equals("class") && !entry.getKey().equals("style")) {
                return "//" + node.getTagName() + "[@" + entry.getKey() + "='" + entry.getValue() + "']";
            }
        }

        return "//" + node.getTagName(); // Weak fallback
    }
}

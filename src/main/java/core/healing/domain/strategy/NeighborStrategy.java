package core.healing.domain.strategy;

import core.healing.domain.model.ElementNode;
import core.healing.utils.SemanticMatcher;

/**
 * NeighborStrategy (Contextual Healing).
 * Validates candidate element by checking its "neighbor" (previous sibling).
 * Useful when the element's own attributes change but it stays next to a static
 * label.
 */
public class NeighborStrategy implements HealingStrategy {

    private final SemanticMatcher matcher = new SemanticMatcher();

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        // We only care if the ORIGINAL (broken element) has expected neighbor info.
        // This info must come from the Target definition or inferred from Locator.
        String expectedPrevText = original.getPrevSiblingText();

        if (expectedPrevText == null || expectedPrevText.isEmpty()) {
            return 0.0;
        }

        String candidatePrevText = candidate.getPrevSiblingText();
        if (candidatePrevText == null || candidatePrevText.isEmpty()) {
            return 0.0;
        }

        // 1. Exact Neighbor Text Match (High Confidence)
        if (expectedPrevText.equals(candidatePrevText)) {
            return 0.95;
        }

        // 2. Fuzzy/Semantic Neighbor Text Match
        double similarity = SemanticMatcher.semanticSimilarity(expectedPrevText, candidatePrevText);
        if (similarity > 0.85) {
            return 0.80; // Good match
        }

        return 0.0;
    }

    @Override
    public String getName() {
        return "NeighborStrategy";
    }

    @Override
    public double getWeight() {
        // Lower than ID/Exact Text but higher than Structure
        return 0.80;
    }
    @Override
    public double getHealingHold() {
        return 0.5;
    }
}

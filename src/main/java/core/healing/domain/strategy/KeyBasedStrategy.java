package core.healing.domain.strategy;

import core.healing.domain.model.ElementNode;
import core.healing.utils.AttributeGroup;
import core.healing.utils.KeyNormalizer;

import java.util.Map;

/**
 * Strategy 5: Key-Based Matching
 * Specialized strategy for matching elements using descriptive keys.
 */
public class KeyBasedStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        if (original.getAttributes() == null || original.getAttributes().isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        int matchCount = 0;

        // For each attribute in original metadata
        for (Map.Entry<String, String> oldAttr : original.getAttributes().entrySet()) {
            String oldAttrName = oldAttr.getKey();
            String oldAttrValue = oldAttr.getValue();

            if (oldAttrValue == null || oldAttrValue.isEmpty()) {
                continue;
            }

            // Only process IDENTITY and LABEL attributes
            AttributeGroup group = AttributeGroup.findGroup(oldAttrName);
            if (group != AttributeGroup.IDENTITY && group != AttributeGroup.LABEL) {
                continue;
            }

            // Try to match against all attributes in the same group
            double bestMatch = 0.0;

            if (group != null) {
                for (String candidateAttrName : group.getAttributes()) {
                    String candidateAttrValue = candidate.getAttribute(candidateAttrName);

                    if (candidateAttrValue != null && !candidateAttrValue.isEmpty()) {
                        // Use KeyNormalizer for intelligent comparison
                        double keySim = KeyNormalizer.keySimilarity(oldAttrValue, candidateAttrValue);
                        bestMatch = Math.max(bestMatch, keySim);

                        // ✅ STRONG KEY MATCH
                        if (bestMatch >= 0.9) {
                            return 0.9;
                        }
                    }
                }
            }

            if (bestMatch > 0) {
                totalScore += bestMatch;
                matchCount++;
            }
        }

        // Add bonus for text/placeholder matching
        double textBonus = 0.0;

        // Check placeholder
        String oldPlaceholder = original.getAttribute("placeholder");
        String candidatePlaceholder = candidate.getAttribute("placeholder");
        if (oldPlaceholder != null && candidatePlaceholder != null) {
            double placeholderSim = KeyNormalizer.keySimilarity(oldPlaceholder, candidatePlaceholder);
            textBonus += placeholderSim;
            matchCount++;
        }

        // Check aria-label
        String oldAriaLabel = original.getAttribute("aria-label");
        String candidateAriaLabel = candidate.getAttribute("aria-label");
        if (oldAriaLabel != null && candidateAriaLabel != null) {
            double ariaSim = KeyNormalizer.keySimilarity(oldAriaLabel, candidateAriaLabel);
            textBonus += ariaSim;
            matchCount++;
        }

        totalScore += textBonus;

        return matchCount > 0 ? totalScore / matchCount : 0.0;
    }

    @Override
    public String getName() {
        return "KeyBased";
    }

    @Override
    public double getWeight() {
        return 0.95; // Very high weight - second only to ExactAttribute
    }
    @Override
    public double getHealingHold() {
        return 0.5;
    }
}

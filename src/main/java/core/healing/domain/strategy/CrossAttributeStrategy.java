package core.healing.domain.strategy;

import core.healing.domain.model.ElementNode;
import core.healing.utils.AttributeGroup;
import core.healing.utils.SimilarityUtil;

import java.util.Map;

/**
 * Strategy 2: Cross-Attribute Identity Match
 * Looks for elements with DIFFERENT attribute names but similar values in the
 * same semantic group.
 * Example: name="username" → id="userId" (both are IDENTITY attributes)
 */
public class CrossAttributeStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        double totalScore = 0;
        int matchCount = 0;

        if (original.getAttributes() == null)
            return 0;

        // For each attribute in old element
        for (Map.Entry<String, String> oldAttr : original.getAttributes().entrySet()) {
            String oldAttrName = oldAttr.getKey();
            String oldAttrValue = oldAttr.getValue();

            if (oldAttrValue == null || oldAttrValue.isEmpty())
                continue;

            // Find which group this attribute belongs to
            AttributeGroup oldGroup = AttributeGroup.findGroup(oldAttrName);
            if (oldGroup == null)
                continue;

            // Try to find a matching attribute in the same group
            double bestMatch = 0;

            for (String candidateAttrName : oldGroup.getAttributes()) {
                String candidateAttrValue = candidate.getAttribute(candidateAttrName);

                if (candidateAttrValue != null && !candidateAttrValue.isEmpty()) {
                    double similarity = SimilarityUtil.similarity(oldAttrValue, candidateAttrValue);
                    bestMatch = Math.max(bestMatch, similarity);
                }
            }

            // ✅ STRONG CROSS-ATTRIBUTE MATCH
            if (bestMatch >= 0.9) {
                return 0.9;
            }

            if (bestMatch > 0) {
                totalScore += bestMatch;
                matchCount++;
            }

        }

        // Add text similarity bonus
        double textSim = SimilarityUtil.similarity(original.getText(), candidate.getText());
        totalScore += textSim * 2;
        matchCount += 2;

        // Add structural similarity bonus
        if (original.getDepth() > 0 && candidate.getDepth() > 0) {
            double depthSim = SimilarityUtil.depthSimilarity(original.getDepth(), candidate.getDepth());
            totalScore += depthSim;
            matchCount++;
        }

        return matchCount > 0 ? totalScore / matchCount : 0;
    }

    @Override
    public String getName() {
        return "CrossAttribute";
    }

    @Override
    public double getWeight() {
        return 0.9; // Slightly lower than exact match
    }

    @Override
    public double getHealingHold() {
        return 0.5;
    }
}

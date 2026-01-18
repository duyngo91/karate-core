package core.healing.domain.strategy;

import core.healing.domain.model.ElementNode;
import core.healing.utils.AttributeGroup;
import core.healing.utils.SemanticMatcher;
import core.healing.utils.SimilarityUtil;

import java.util.Map;

/**
 * Strategy 3: Semantic Value Match
 * Uses semantic understanding to match values that mean the same thing.
 * Example: name="username" → name="userId" (both mean user identifier)
 */
public class SemanticValueStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        double totalScore = 0;
        int matchCount = 0;

        if (original.getAttributes() == null)
            return 0;

        // Check semantic similarity for identity attributes
        for (Map.Entry<String, String> oldAttr : original.getAttributes().entrySet()) {
            String oldAttrName = oldAttr.getKey();
            String oldAttrValue = oldAttr.getValue();

            if (oldAttrValue == null || oldAttrValue.isEmpty())
                continue;

            AttributeGroup group = AttributeGroup.findGroup(oldAttrName);
            if (group != AttributeGroup.IDENTITY && group != AttributeGroup.LABEL) {
                continue; // Only check identity and label attributes
            }

            // Try both same attribute and cross-attribute
            double bestSemantic = 0;

            // Same attribute name
            String candidateValue = candidate.getAttribute(oldAttrName);
            if (candidateValue != null) {
                double semantic = SemanticMatcher.semanticSimilarity(oldAttrValue, candidateValue);
                bestSemantic = Math.max(bestSemantic, semantic);
            }

            // Cross-attribute in same group
            if (group != null) {
                for (String attrName : group.getAttributes()) {
                    candidateValue = candidate.getAttribute(attrName);
                    if (candidateValue != null) {
                        double semantic = SemanticMatcher.semanticSimilarity(oldAttrValue, candidateValue);
                        bestSemantic = Math.max(bestSemantic, semantic);
                    }
                }
            }

            if (bestSemantic > 0) {
                totalScore += bestSemantic;
                matchCount++;
            }
        }

        // Add text similarity
        double textSim = SimilarityUtil.similarity(original.getText(), candidate.getText());
        totalScore += textSim * 1.5;
        matchCount += 1.5;

        // Add placeholder/label semantic matching
        String oldPlaceholder = original.getAttribute("placeholder");
        String newPlaceholder = candidate.getAttribute("placeholder");
        if (oldPlaceholder != null && newPlaceholder != null) {
            double placeholderSem = SemanticMatcher.semanticSimilarity(oldPlaceholder, newPlaceholder);
            totalScore += placeholderSem;
            matchCount++;
        }

        return matchCount > 0 ? totalScore / matchCount : 0;
    }

    @Override
    public String getName() {
        return "SemanticValue";
    }

    @Override
    public double getWeight() {
        return 0.85; // Good but less reliable than exact/cross-attribute
    }
    @Override
    public double getHealingHold() {
        return 0.5;
    }
}

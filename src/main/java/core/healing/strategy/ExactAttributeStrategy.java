package core.healing.strategy;

import core.healing.model.ElementNode;
import core.healing.utils.SimilarityUtil;
import java.util.Map;

/**
 * Strategy 1: Exact Attribute Match
 * Looks for elements with the SAME attribute name but similar values.
 * Example: name="username" → name="usrName"
 */
public class ExactAttributeStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        double score = 0;
        int attributeCount = 0;

        // Check all identity attributes from old element
        if (original.getAttributes() != null) {
            for (Map.Entry<String, String> entry : original.getAttributes().entrySet()) {
                String attrName = entry.getKey();
                String oldValue = entry.getValue();
                String newValue = candidate.getAttribute(attrName);

                if (oldValue != null && newValue != null) {
                    double attrSim = SimilarityUtil.similarity(oldValue, newValue);
                    score += attrSim;
                    attributeCount++;
                }
            }
        }

        // Add text similarity
        double textSim = SimilarityUtil.similarity(original.getText(), candidate.getText());
        score += textSim * 2; // Weight text higher
        attributeCount += 2;

        // Add class similarity
        String oldClass = original.getAttribute("class");
        String newClass = candidate.getAttribute("class");
        if (oldClass != null && newClass != null) {
            double classSim = SimilarityUtil.similarity(oldClass, newClass);
            score += classSim;
            attributeCount++;
        }

        return attributeCount > 0 ? score / attributeCount : 0;
    }

    @Override
    public String getName() {
        return "ExactAttribute";
    }

    @Override
    public double getWeight() {
        return 1.0; // Highest weight - most reliable
    }
}

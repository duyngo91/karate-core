package core.healing.strategy;

import core.healing.model.ElementNode;
import core.healing.utils.SimilarityUtil;

/**
 * Strategy 4: Structural + Text Match
 * Matches based on structure and text when attributes are unreliable.
 * Uses: tag, text, depth, parentTag
 */
public class StructuralStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        double score = 0;
        int factors = 0;

        // Tag must match (required)
        String candidateTag = candidate.getTagName();
        if (original.getTagName() == null || candidateTag == null ||
                !original.getTagName().equalsIgnoreCase(candidateTag)) {
            return 0; // Different tag = not a match
        }

        // Text similarity (high weight)
        double textSim = SimilarityUtil.similarity(original.getText(), candidate.getText());
        score += textSim * 3;
        factors += 3;

        // Placeholder similarity (for inputs)
        String oldPlaceholder = original.getAttribute("placeholder");
        String newPlaceholder = candidate.getAttribute("placeholder");
        if (oldPlaceholder != null && newPlaceholder != null) {
            double placeholderSim = SimilarityUtil.similarity(oldPlaceholder, newPlaceholder);
            score += placeholderSim * 2;
            factors += 2;
        }

        // Depth similarity
        if (original.getDepth() > 0 && candidate.getDepth() > 0) {
            double depthSim = SimilarityUtil.depthSimilarity(original.getDepth(), candidate.getDepth());
            score += depthSim;
            factors++;
        }

        // Parent tag similarity
        if (original.getParentTag() != null) {
            String parentTag = candidate.getParentTag();
            if (parentTag != null && original.getParentTag().equalsIgnoreCase(parentTag)) {
                score += 1.0; // Exact parent match
                factors++;
            }
        }

        // Type attribute (for inputs)
        String oldType = original.getAttribute("type");
        String newType = candidate.getAttribute("type");
        if (oldType != null && newType != null && oldType.equalsIgnoreCase(newType)) {
            score += 1.0;
            factors++;
        }

        return factors > 0 ? score / factors : 0;
    }

    @Override
    public String getName() {
        return "Structural";
    }

    @Override
    public double getWeight() {
        return 0.75; // Fallback strategy - lower weight
    }
}

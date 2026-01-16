package core.healing.strategy;

import core.healing.model.ElementNode;
import core.healing.utils.TextNormalizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Strategy 6: Text-Based Matching
 * Specialized strategy for matching elements based on their visible text
 * content.
 */
public class TextBasedStrategy implements HealingStrategy {

    // Tags that primarily contain text content
    private static final Set<String> TEXT_TAGS = new HashSet<>(Arrays.asList(
            "label", "span", "div", "p", "a", "button",
            "h1", "h2", "h3", "h4", "h5", "h6",
            "td", "th", "li", "dt", "dd", "legend", "caption"));

    // Heading tags (for grouping)
    private static final Set<String> HEADING_TAGS = new HashSet<>(Arrays.asList(
            "h1", "h2", "h3", "h4", "h5", "h6"));

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        // Only apply to text-containing tags
        if (!isTextTag(original.getTagName())) {
            return 0.0;
        }

        String candidateTag = candidate.getTagName().toLowerCase();
        if (!isTextTag(candidateTag)) {
            return 0.0;
        }

        // Get text content
        String oldText = original.getText();
        String candidateText = candidate.getText();

        if (oldText == null || oldText.isEmpty()) {
            return 0.0;
        }

        if (candidateText == null || candidateText.isEmpty()) {
            return 0.0;
        }

        // Calculate text similarity
        double textScore = TextNormalizer.textSimilarity(oldText, candidateText);

        // Apply tag weight
        double tagWeight = calculateTagWeight(original.getTagName(), candidateTag);

        double finalScore = textScore * tagWeight;

        // ✅ TEXT RÕ RÀNG → QUYẾT LUÔN
        if (finalScore >= 0.85) {
            return 0.85;
        }

        return finalScore;

    }

    private boolean isTextTag(String tag) {
        return tag != null && TEXT_TAGS.contains(tag.toLowerCase());
    }

    private double calculateTagWeight(String tag1, String tag2) {
        if (tag1 == null || tag2 == null)
            return 0.0;
        String t1 = tag1.toLowerCase();
        String t2 = tag2.toLowerCase();

        // Exact same tag
        if (t1.equals(t2)) {
            return 1.0;
        }

        // Both are headings
        if (HEADING_TAGS.contains(t1) && HEADING_TAGS.contains(t2)) {
            return 0.95;
        }

        // Different tags but both text-containing
        return 0.90;
    }

    @Override
    public String getName() {
        return "TextBased";
    }

    @Override
    public double getWeight() {
        return 0.92; // High weight - text is very reliable for these elements
    }
    @Override
    public double getHealingHold() {
        return 0.5;
    }
}

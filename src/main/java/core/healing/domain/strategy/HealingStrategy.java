package core.healing.domain.strategy;

import core.healing.domain.model.ElementNode;

/**
 * Interface for different healing strategies.
 * Each strategy represents a different approach to matching elements.
 * Framework agnostic: uses ElementNode instead of Selenium WebElement.
 */
public interface HealingStrategy {

    /**
     * Calculate a score for how well the candidate matches the old element.
     * 
     * @param original  Metadata from the original element (snapshot)
     * @param candidate The candidate element to score (current page node)
     * @return Score between 0.0 and 1.0 (higher is better match)
     */
    double score(ElementNode original, ElementNode candidate);

    /**
     * Get the name of this strategy for logging/debugging
     */
    String getName();

    /**
     * Get the weight/priority of this strategy.
     * Higher weight means this strategy is more reliable.
     * 
     * @return Weight between 0.0 and 1.0
     */
    default double getWeight() {
        return 1.0;
    }
    double getHealingHold();
}

package core.healing.strategy;

import core.healing.model.ElementNode;
import core.healing.rag.ElementMetadata;
import core.healing.rag.GoldenStateRecorder;

/**
 * Strategy 9: Location Healing
 * Matches elements based on geometric proximity to their original position.
 * Useful when an element's look changes but it stays in the same place.
 */
public class LocationHealingStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        // 1. Get Golden State to retrieve original coordinates
        String key = original.getElementId();
        if (key == null)
            key = original.getAttribute("id");
        if (key == null)
            key = original.getAttribute("name");

        if (key == null)
            return 0.0;

        ElementMetadata meta = GoldenStateRecorder.getInstance().getMetadata(key);
        if (meta == null) {
            return 0.0;
        }

        // 2. Check if we have valid coordinates
        if (meta.getWidth() == 0 || meta.getHeight() == 0) {
            return 0.0; // No location data recorded
        }

        // 3. Calculate Distance
        // Function: 1 / (1 + distance)
        // distance = sqrt((x1-x2)^2 + (y1-y2)^2)

        double dist = Math.sqrt(Math.pow(meta.getX() - candidate.getX(), 2) +
                Math.pow(meta.getY() - candidate.getY(), 2));

        // Tolerance: If within 50px, high score. If far, low score.
        // We normalize this. 0 distance = 1.0. 1000px distance ~= 0.
        // Let's use a Gaussian-like decay or simple inverse.

        if (dist < 10)
            return 1.0; // Almost exact match
        if (dist < 50)
            return 0.8; // Very close
        if (dist < 100)
            return 0.5; // Close
        if (dist < 200)
            return 0.2; // Somewhat near

        return 0.0; // Too far
    }

    @Override
    public String getName() {
        return "Location";
    }

    @Override
    public double getWeight() {
        return 0.6; // Lower weight because position is fragile (responsive design)
    }
}

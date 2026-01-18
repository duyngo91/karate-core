package core.healing.domain.strategy;

import core.healing.application.port.IHealingDriver;
import core.healing.domain.model.ElementNode;
import core.healing.infrastructure.visual.VisualService;
import core.platform.utils.Logger;

import java.awt.image.BufferedImage;

/**
 * Strategy 10: Visual Healing (Screenshot Comparison using SSIM)
 * Compares visual appearance of elements using Structural Similarity Index.
 */
public class VisualHealingStrategy implements HealingStrategy {

    private final VisualService visualService;
    private IHealingDriver driver;

    public VisualHealingStrategy() {
        this.visualService = VisualService.getInstance();
    }

    public void setDriver(IHealingDriver driver) {
        this.driver = driver;
    }

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        // If Visual Service not available (OpenCV failed to load), skip
        if (!visualService.isAvailable()) {
            return 0.0;
        }

        // Get element ID from original
        String elementId = original.getElementId();
        if (elementId == null) {
            // Fallback to ID or name attribute
            elementId = original.getAttribute("id");
            if (elementId == null) {
                elementId = original.getAttribute("name");
            }
        }

        if (elementId == null) {
            Logger.debug("Visual Healing: No element ID available for comparison");
            return 0.0;
        }

        try {
            // Load golden screenshot from disk
            BufferedImage goldenImage = visualService.loadScreenshot(elementId);
            if (goldenImage == null) {
                Logger.debug("Visual Healing: No golden screenshot found for %s", elementId);
                return 0.0;
            }

            // Get candidate screenshot from memory (or capture if missing - Lazy Capture)
            BufferedImage candidateImage = candidate.getScreenshot();
            if (candidateImage == null && driver != null) {
                Logger.debug("Lazy Capture: Capturing screenshot for candidate %s", elementId);
                String locator = CandidateFinder.constructTempLocator(candidate);
                if (locator != null) {
                    candidateImage = visualService.captureScreenshot(driver, locator);
                    candidate.setScreenshot(candidateImage);
                }
            }

            if (candidateImage == null) {
                Logger.debug("Visual Healing: No candidate screenshot available for %s", elementId);
                return 0.0;
            }

            // Calculate SSIM
            double ssim = visualService.calculateSSIM(goldenImage, candidateImage);
            Logger.debug("Visual SSIM: %.3f for element %s", ssim, elementId);

            return ssim; // Return raw SSIM score (0.0 to 1.0)

        } catch (Exception e) {
            Logger.error("Visual Healing error: %s", e.getMessage());
            return 0.0;
        }
    }


    @Override
    public String getName() {
        return "VisualHealingStrategy";
    }

    @Override
    public double getWeight() {
        // Medium-low weight (0.35)
        // Visual matching is powerful but computationally expensive
        // Use as supporting evidence rather than primary strategy
        return 0.35;
    }

    /**
     * Calculate SSIM score between golden and current screenshots.
     * This method is here for when we can capture candidate screenshots.
     */
    private double calculateVisualSimilarity(BufferedImage golden, BufferedImage current) {
        if (golden == null || current == null) {
            return 0.0;
        }

        double ssim = visualService.calculateSSIM(golden, current);
        Logger.debug("Visual SSIM score: %.3f", ssim);
        return ssim;
    }
    @Override
    public double getHealingHold() {
        return 0.5;
    }
}

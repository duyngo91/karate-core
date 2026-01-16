package core.healing.strategy;

import core.healing.model.ElementNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VisualHealingStrategy
 * Tests image similarity-based matching using SSIM
 */
@DisplayName("VisualHealingStrategy Tests")
class VisualHealingStrategyTest {

    private VisualHealingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new VisualHealingStrategy();
    }

    @Test
    @DisplayName("Should return zero when original has no screenshot")
    void shouldReturnZeroWhenOriginalHasNoScreenshot() {
        // Given
        ElementNode original = new ElementNode();
        original.setTagName("button");
        // No screenshot

        ElementNode candidate = new ElementNode();
        candidate.setTagName("button");
        candidate.setScreenshot(createDummyImage(100, 50));

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertEquals(0.0, score, 0.001, "Missing original screenshot should return zero");
    }

    @Test
    @DisplayName("Should return zero when candidate has no screenshot")
    void shouldReturnZeroWhenCandidateHasNoScreenshot() {
        // Given
        ElementNode original = new ElementNode();
        original.setTagName("button");
        original.setScreenshot(createDummyImage(100, 50));

        ElementNode candidate = new ElementNode();
        candidate.setTagName("button");
        // No screenshot

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertEquals(0.0, score, 0.001, "Missing candidate screenshot should return zero");
    }

    @Test
    @DisplayName("Should return perfect score for identical images")
    void shouldReturnPerfectScoreForIdenticalImages() {
        // Given
        BufferedImage image = createSolidColorImage(100, 50, 255, 0, 0); // Red
        
        ElementNode original = new ElementNode();
        original.setTagName("button");
        original.setScreenshot(image);

        ElementNode candidate = new ElementNode();
        candidate.setTagName("button");
        candidate.setScreenshot(copyImage(image));

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.95, "Identical images should have very high SSIM score");
    }

    @Test
    @DisplayName("Should return low score for completely different images")
    void shouldReturnLowScoreForCompletelyDifferentImages() {
        // Given
        BufferedImage redImage = createSolidColorImage(100, 50, 255, 0, 0);
        BufferedImage blueImage = createSolidColorImage(100, 50, 0, 0, 255);

        ElementNode original = new ElementNode();
        original.setTagName("button");
        original.setScreenshot(redImage);

        ElementNode candidate = new ElementNode();
        candidate.setTagName("button");
        candidate.setScreenshot(blueImage);

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score < 0.5, "Completely different images should have low score");
    }

    @Test
    @DisplayName("Should handle different image sizes")
    void shouldHandleDifferentImageSizes() {
        // Given
        BufferedImage smallImage = createSolidColorImage(50, 25, 255, 0, 0);
        BufferedImage largeImage = createSolidColorImage(200, 100, 255, 0, 0);

        ElementNode original = new ElementNode();
        original.setTagName("button");
        original.setScreenshot(smallImage);

        ElementNode candidate = new ElementNode();
        candidate.setTagName("button");
        candidate.setScreenshot(largeImage);

        // When & Then
        assertDoesNotThrow(() -> strategy.score(original, candidate),
                "Should handle different image sizes without throwing exception");
    }

    @Test
    @DisplayName("Should handle slightly modified images")
    void shouldHandleSlightlyModifiedImages() {
        // Given: Two similar but not identical images
        BufferedImage original1 = createSolidColorImage(100, 50, 255, 0, 0);
        BufferedImage original2 = createSolidColorImage(100, 50, 250, 5, 5); // Slightly different

        ElementNode original = new ElementNode();
        original.setTagName("button");
        original.setScreenshot(original1);

        ElementNode candidate = new ElementNode();
        candidate.setTagName("button");
        candidate.setScreenshot(original2);

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.8, "Slightly modified images should have high similarity");
    }

    @Test
    @DisplayName("Should verify strategy name")
    void shouldVerifyStrategyName() {
        assertEquals("Visual", strategy.getName());
    }

    @Test
    @DisplayName("Should verify strategy weight")
    void shouldVerifyStrategyWeight() {
        assertEquals(0.35, strategy.getWeight(), 0.001);
    }

    @Test
    @DisplayName("Should verify healing threshold")
    void shouldVerifyHealingThreshold() {
        assertEquals(0.8, strategy.getHealingHold(), 0.001);
    }

    @Test
    @DisplayName("Should handle null images gracefully")
    void shouldHandleNullImagesGracefully() {
        // Given
        ElementNode original = new ElementNode();
        original.setTagName("button");
        original.setScreenshot(null);

        ElementNode candidate = new ElementNode();
        candidate.setTagName("button");
        candidate.setScreenshot(null);

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertEquals(0.0, score, 0.001, "Null images should return zero");
    }

    // Helper methods
    private BufferedImage createDummyImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    private BufferedImage createSolidColorImage(int width, int height, int r, int g, int b) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgb = (r << 16) | (g << 8) | b;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                source.getType()
        );
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                copy.setRGB(x, y, source.getRGB(x, y));
            }
        }
        return copy;
    }
}

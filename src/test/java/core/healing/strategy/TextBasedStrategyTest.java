package core.healing.strategy;

import core.healing.model.ElementNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TextBasedStrategy
 * Tests text content matching with tag weight consideration
 */
@DisplayName("TextBasedStrategy Tests")
class TextBasedStrategyTest {

//    private TextBasedStrategy strategy;
//
//    @BeforeEach
//    void setUp() {
//        strategy = new TextBasedStrategy();
//    }
//
//    @Test
//    @DisplayName("Should return perfect score for exact text match")
//    void shouldReturnPerfectScoreForExactTextMatch() {
//        // Given
//        ElementNode original = createNodeWithText("button", "Submit Form");
//        ElementNode candidate = createNodeWithText("button", "Submit Form");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Exact text match should return perfect score");
//    }
//
//    @Test
//    @DisplayName("Should handle similar text")
//    void shouldHandleSimilarText() {
//        // Given
//        ElementNode original = createNodeWithText("button", "Submit Form");
//        ElementNode candidate = createNodeWithText("button", "Submit Form Now");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.7 && score < 1.0, "Similar text should have high score");
//    }
//
//    @Test
//    @DisplayName("Should prioritize label tags")
//    void shouldPrioritizeLabelTags() {
//        // Given
//        ElementNode original = createNodeWithText("label", "Username");
//        ElementNode candidate = createNodeWithText("label", "Username");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Label tags should have high priority");
//    }
//
//    @Test
//    @DisplayName("Should prioritize span tags")
//    void shouldPrioritizeSpanTags() {
//        // Given
//        ElementNode original = createNodeWithText("span", "Error message");
//        ElementNode candidate = createNodeWithText("span", "Error message");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Span tags should have high priority");
//    }
//
//    @Test
//    @DisplayName("Should handle button text")
//    void shouldHandleButtonText() {
//        // Given
//        ElementNode original = createNodeWithText("button", "Click Me");
//        ElementNode candidate = createNodeWithText("button", "Click Me");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Button text should match exactly");
//    }
//
//    @ParameterizedTest
//    @DisplayName("Should handle text similarity variations")
//    @CsvSource({
//            "Login, Login, 1.0",
//            "Login, Login Now, 0.70",
//            "Submit, Submit Form, 0.65",
//            "Click, Click Here, 0.60",
//            "Save, Cancel, 0.20"
//    })
//    void shouldHandleTextSimilarityVariations(String originalText, String candidateText, double minScore) {
//        // Given
//        ElementNode original = createNodeWithText("button", originalText);
//        ElementNode candidate = createNodeWithText("button", candidateText);
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score >= minScore - 0.15,
//                String.format("Score %.2f should be >= %.2f for '%s' vs '%s'",
//                        score, minScore, originalText, candidateText));
//    }
//
//    @Test
//    @DisplayName("Should be case-insensitive")
//    void shouldBeCaseInsensitive() {
//        // Given
//        ElementNode original = createNodeWithText("button", "SUBMIT");
//        ElementNode candidate = createNodeWithText("button", "submit");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.9, "Should be case-insensitive");
//    }
//
//    @Test
//    @DisplayName("Should handle whitespace variations")
//    void shouldHandleWhitespaceVariations() {
//        // Given
//        ElementNode original = createNodeWithText("button", "  Submit  Form  ");
//        ElementNode candidate = createNodeWithText("button", "Submit Form");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.95, "Should normalize whitespace");
//    }
//
//    @Test
//    @DisplayName("Should handle empty text")
//    void shouldHandleEmptyText() {
//        // Given
//        ElementNode original = createNodeWithText("div", "");
//        ElementNode candidate = createNodeWithText("div", "Some text");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(0.0, score, 0.001, "Empty text should return zero score");
//    }
//
//    @Test
//    @DisplayName("Should handle null text")
//    void shouldHandleNullText() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("div");
//        // No text set
//
//        ElementNode candidate = createNodeWithText("div", "Some text");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(0.0, score, 0.001, "Null text should return zero score");
//    }
//
//    @Test
//    @DisplayName("Should handle placeholder attribute")
//    void shouldHandlePlaceholderAttribute() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("input");
//        original.addAttribute("placeholder", "Enter username");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("input");
//        candidate.addAttribute("placeholder", "Enter username");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.8, "Should match placeholder text");
//    }
//
//    @Test
//    @DisplayName("Should handle aria-label attribute")
//    void shouldHandleAriaLabel() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("button");
//        original.addAttribute("aria-label", "Close dialog");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("button");
//        candidate.addAttribute("aria-label", "Close dialog");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.8, "Should match aria-label");
//    }
//
//    @Test
//    @DisplayName("Should handle title attribute")
//    void shouldHandleTitleAttribute() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("a");
//        original.addAttribute("title", "Go to homepage");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("a");
//        candidate.addAttribute("title", "Go to homepage");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.8, "Should match title attribute");
//    }
//
//    @Test
//    @DisplayName("Should prefer visible text over attributes")
//    void shouldPreferVisibleTextOverAttributes() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("button");
//        original.setText("Submit");
//        original.addAttribute("aria-label", "Different label");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("button");
//        candidate.setText("Submit");
//        candidate.addAttribute("aria-label", "Another label");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Should prioritize visible text");
//    }
//
//    @Test
//    @DisplayName("Should handle long text")
//    void shouldHandleLongText() {
//        // Given
//        String longText = "This is a very long text that contains multiple words and should be handled correctly by the strategy";
//        ElementNode original = createNodeWithText("p", longText);
//        ElementNode candidate = createNodeWithText("p", longText);
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Should handle long text correctly");
//    }
//
//    @Test
//    @DisplayName("Should verify strategy name")
//    void shouldVerifyStrategyName() {
//        assertEquals("TextBased", strategy.getName());
//    }
//
//    @Test
//    @DisplayName("Should verify strategy weight")
//    void shouldVerifyStrategyWeight() {
//        assertEquals(0.92, strategy.getWeight(), 0.001);
//    }
//
//    @Test
//    @DisplayName("Should verify healing threshold")
//    void shouldVerifyHealingThreshold() {
//        assertEquals(0.7, strategy.getHealingHold(), 0.001);
//    }
//
//    // Helper method
//    private ElementNode createNodeWithText(String tag, String text) {
//        ElementNode node = new ElementNode();
//        node.setTagName(tag);
//        node.setText(text);
//        return node;
//    }
}

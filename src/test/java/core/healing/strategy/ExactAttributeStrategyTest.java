package core.healing.strategy;

import core.healing.model.ElementNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExactAttributeStrategy
 * Tests exact attribute matching and similarity scoring
 */
@DisplayName("ExactAttributeStrategy Tests")
class ExactAttributeStrategyTest {

//    private ExactAttributeStrategy strategy;
//
//    @BeforeEach
//    void setUp() {
//        strategy = new ExactAttributeStrategy();
//    }
//
//    @Test
//    @DisplayName("Should return perfect score for exact ID match")
//    void shouldReturnPerfectScoreForExactIdMatch() {
//        // Given
//        ElementNode original = createNode("button", "login-button", "btn-primary", "Login");
//        ElementNode candidate = createNode("button", "login-button", "btn-primary", "Login");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Exact ID match should return perfect score");
//    }
//
//    @Test
//    @DisplayName("Should return perfect score for exact name match")
//    void shouldReturnPerfectScoreForExactNameMatch() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("input");
//        original.addAttribute("name", "username");
//        original.addAttribute("type", "text");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("input");
//        candidate.addAttribute("name", "username");
//        candidate.addAttribute("type", "text");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Exact name match should return perfect score");
//    }
//
//    @Test
//    @DisplayName("Should return perfect score for exact text match")
//    void shouldReturnPerfectScoreForExactTextMatch() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("button");
//        original.setText("Submit Form");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("button");
//        candidate.setText("Submit Form");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001, "Exact text match should return perfect score");
//    }
//
//    @Test
//    @DisplayName("Should return zero for completely different attributes")
//    void shouldReturnZeroForCompletelyDifferentAttributes() {
//        // Given
//        ElementNode original = createNode("button", "login-button", "btn-primary", "Login");
//        ElementNode candidate = createNode("a", "logout-link", "link-secondary", "Logout");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score < 0.3, "Completely different attributes should have low score");
//    }
//
//    @Test
//    @DisplayName("Should handle similar but not exact ID match")
//    void shouldHandleSimilarButNotExactIdMatch() {
//        // Given
//        ElementNode original = createNode("button", "submit-btn-v1", "btn-primary", "Submit");
//        ElementNode candidate = createNode("button", "submit-btn-v2", "btn-primary", "Submit");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.7 && score < 1.0,
//                "Similar IDs should have high but not perfect score. Got: " + score);
//    }
//
//    @ParameterizedTest
//    @DisplayName("Should score different similarity levels correctly")
//    @CsvSource({
//            "login-button, login-button, 1.0",
//            "login-button, login-btn, 0.85",
//            "submit-form, submit-form-v2, 0.80",
//            "username, password, 0.30"
//    })
//    void shouldScoreDifferentSimilarityLevels(String originalId, String candidateId, double expectedMinScore) {
//        // Given
//        ElementNode original = new ElementNode();
//        original.addAttribute("id", originalId);
//
//        ElementNode candidate = new ElementNode();
//        candidate.addAttribute("id", candidateId);
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score >= expectedMinScore - 0.15,
//                String.format("Score %.2f should be >= %.2f for '%s' vs '%s'",
//                        score, expectedMinScore, originalId, candidateId));
//    }
//
//    @Test
//    @DisplayName("Should handle null attributes gracefully")
//    void shouldHandleNullAttributesGracefully() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("button");
//        original.setText("Click me");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("button");
//        // No text set
//
//        // When & Then
//        assertDoesNotThrow(() -> strategy.score(original, candidate),
//                "Should handle null attributes without throwing exception");
//    }
//
//    @Test
//    @DisplayName("Should handle empty elements")
//    void shouldHandleEmptyElements() {
//        // Given
//        ElementNode original = new ElementNode();
//        ElementNode candidate = new ElementNode();
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(0.0, score, 0.001, "Empty elements should have zero score");
//    }
//
//    @Test
//    @DisplayName("Should prioritize data-testid attribute")
//    void shouldPrioritizeDataTestId() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.addAttribute("data-testid", "submit-button");
//        original.addAttribute("id", "different-id");
//
//        ElementNode candidate = new ElementNode();
//        candidate.addAttribute("data-testid", "submit-button");
//        candidate.addAttribute("id", "another-id");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001,
//                "Exact data-testid match should return perfect score");
//    }
//
//    @Test
//    @DisplayName("Should handle multiple matching attributes")
//    void shouldHandleMultipleMatchingAttributes() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.addAttribute("id", "login-form");
//        original.addAttribute("name", "loginForm");
//        original.addAttribute("class", "form-container");
//        original.setText("Login Form");
//
//        ElementNode candidate = new ElementNode();
//        candidate.addAttribute("id", "login-form");
//        candidate.addAttribute("name", "loginForm");
//        candidate.addAttribute("class", "form-container");
//        candidate.setText("Login Form");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(1.0, score, 0.001,
//                "Multiple exact matches should return perfect score");
//    }
//
//    @Test
//    @DisplayName("Should verify strategy name")
//    void shouldVerifyStrategyName() {
//        assertEquals("ExactAttribute", strategy.getName());
//    }
//
//    @Test
//    @DisplayName("Should verify strategy weight")
//    void shouldVerifyStrategyWeight() {
//        assertEquals(1.0, strategy.getWeight(), 0.001);
//    }
//
//    @Test
//    @DisplayName("Should verify healing threshold")
//    void shouldVerifyHealingThreshold() {
//        assertEquals(0.8, strategy.getHealingHold(), 0.001);
//    }
//
//    // Helper method
//    private ElementNode createNode(String tag, String id, String className, String text) {
//        ElementNode node = new ElementNode();
//        node.setTagName(tag);
//        if (id != null) node.addAttribute("id", id);
//        if (className != null) node.addAttribute("class", className);
//        if (text != null) node.setText(text);
//        return node;
//    }
}

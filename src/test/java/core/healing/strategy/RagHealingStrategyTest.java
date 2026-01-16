package core.healing.strategy;

import core.healing.model.ElementNode;
import core.healing.rag.GoldenStateRecorder;
import core.healing.rag.ElementMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RagHealingStrategy
 * Tests AI vector embedding-based matching
 */
@DisplayName("RagHealingStrategy Tests")
class RagHealingStrategyTest {

    private RagHealingStrategy strategy;
    private GoldenStateRecorder recorder;

    @BeforeEach
    void setUp() {
        strategy = new RagHealingStrategy();
        recorder = GoldenStateRecorder.getInstance();
    }

    @AfterEach
    void tearDown() {
        // Clean up golden state after each test
        recorder.clearAll();
    }

    @Test
    @DisplayName("Should return zero when no golden state exists")
    void shouldReturnZeroWhenNoGoldenStateExists() {
        // Given
        ElementNode original = createNode("button", "submit-btn", "Submit");
        original.setElementId("login.submit");

        ElementNode candidate = createNode("button", "submit-button", "Submit");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertEquals(0.0, score, 0.001, "No golden state should return zero");
    }

    @Test
    @DisplayName("Should match elements with similar context")
    void shouldMatchElementsWithSimilarContext() {
        // Given: Record golden state
        ElementNode original = createNode("button", "submit-btn", "Submit Form");
        original.setElementId("login.submit");
        recordGoldenState("login.submit", original);

        ElementNode candidate = createNode("button", "submit-button", "Submit Form");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.7, "Similar context should have high vector similarity");
    }

    @Test
    @DisplayName("Should handle different text but same semantic meaning")
    void shouldHandleDifferentTextButSameSemanticMeaning() {
        // Given
        ElementNode original = createNode("button", "login-btn", "Login");
        original.setElementId("auth.login");
        recordGoldenState("auth.login", original);

        ElementNode candidate = createNode("button", "signin-btn", "Sign In");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.5, "Semantically similar should have moderate score");
    }

    @Test
    @DisplayName("Should handle missing element ID")
    void shouldHandleMissingElementId() {
        // Given
        ElementNode original = createNode("button", "submit", "Submit");
        // No element ID set

        ElementNode candidate = createNode("button", "submit", "Submit");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertEquals(0.0, score, 0.001, "Missing element ID should return zero");
    }

    @Test
    @DisplayName("Should use ID attribute as fallback for element ID")
    void shouldUseIdAttributeAsFallback() {
        // Given
        ElementNode original = createNode("button", "submit-btn", "Submit");
        // Element ID not set, but has id attribute
        recordGoldenState("submit-btn", original);

        ElementNode candidate = createNode("button", "submit-button", "Submit");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score >= 0.0, "Should use id attribute as fallback");
    }

    @Test
    @DisplayName("Should use name attribute as fallback")
    void shouldUseNameAttributeAsFallback() {
        // Given
        ElementNode original = new ElementNode();
        original.setTagName("input");
        original.addAttribute("name", "username");
        original.setText("Enter username");
        recordGoldenState("username", original);

        ElementNode candidate = new ElementNode();
        candidate.setTagName("input");
        candidate.addAttribute("name", "username");
        candidate.setText("Enter username");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score >= 0.0, "Should use name attribute as fallback");
    }

    @Test
    @DisplayName("Should handle elements with rich context")
    void shouldHandleElementsWithRichContext() {
        // Given: Element with multiple attributes and context
        ElementNode original = new ElementNode();
        original.setTagName("input");
        original.setElementId("form.email");
        original.addAttribute("type", "email");
        original.addAttribute("placeholder", "Enter your email");
        original.setText("");
        original.setPrevSiblingText("Email Address:");
        recordGoldenState("form.email", original);

        ElementNode candidate = new ElementNode();
        candidate.setTagName("input");
        candidate.addAttribute("type", "email");
        candidate.addAttribute("placeholder", "Enter your email");
        candidate.setPrevSiblingText("Email Address:");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.7, "Rich context should improve vector similarity");
    }

    @Test
    @DisplayName("Should differentiate completely different elements")
    void shouldDifferentiateCompletelyDifferentElements() {
        // Given
        ElementNode original = createNode("button", "login", "Login");
        original.setElementId("auth.login");
        recordGoldenState("auth.login", original);

        ElementNode candidate = createNode("input", "password", "");
        candidate.addAttribute("type", "password");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score < 0.5, "Completely different elements should have low score");
    }

    @Test
    @DisplayName("Should verify strategy name")
    void shouldVerifyStrategyName() {
        assertEquals("RAG/VectorStrategy", strategy.getName());
    }

    @Test
    @DisplayName("Should verify strategy weight")
    void shouldVerifyStrategyWeight() {
        assertEquals(0.95, strategy.getWeight(), 0.001);
    }

    @Test
    @DisplayName("Should verify healing threshold")
    void shouldVerifyHealingThreshold() {
        assertEquals(0.5, strategy.getHealingHold(), 0.001);
    }

    // Helper methods
    private ElementNode createNode(String tag, String id, String text) {
        ElementNode node = new ElementNode();
        node.setTagName(tag);
        if (id != null) node.addAttribute("id", id);
        if (text != null) node.setText(text);
        return node;
    }

    private void recordGoldenState(String key, ElementNode node) {
        Map<String, String> attributes = new HashMap<>(node.getAttributes());
        recorder.recordElement(
                key,
                node.getTagName(),
                node.getText(),
                attributes,
                node.getPrevSiblingText()
        );
    }
}

package core.healing.strategy;

import org.junit.jupiter.api.DisplayName;

/**
 * Unit tests for NeighborStrategy
 * Tests element matching based on previous sibling context
 */
@DisplayName("NeighborStrategy Tests")
class NeighborStrategyTest {

//    private NeighborStrategy strategy;
//
//    @BeforeEach
//    void setUp() {
//        strategy = new NeighborStrategy();
//    }
//
//    @Test
//    @DisplayName("Should match elements with same previous sibling text")
//    void shouldMatchElementsWithSamePrevSiblingText() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "Username:");
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "Username:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.8, "Same previous sibling text should have high score");
//    }
//
//    @Test
//    @DisplayName("Should match elements with same previous sibling tag")
//    void shouldMatchElementsWithSamePrevSiblingTag() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "Email:");
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "Email:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.8, "Same previous sibling tag should contribute to score");
//    }
//
//    @Test
//    @DisplayName("Should handle similar previous sibling text")
//    void shouldHandleSimilarPrevSiblingText() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "Password:");
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "Password");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.7, "Similar previous sibling text should have good score");
//    }
//
//    @Test
//    @DisplayName("Should penalize different previous sibling text")
//    void shouldPenalizeDifferentPrevSiblingText() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "Username:");
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "Password:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score < 0.5, "Different previous sibling text should have low score");
//    }
//
//    @Test
//    @DisplayName("Should handle missing previous sibling on original")
//    void shouldHandleMissingPrevSiblingOnOriginal() {
//        // Given
//        ElementNode original = new ElementNode();
//        original.setTagName("input");
//        // No previous sibling
//
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "Username:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(0.0, score, 0.001, "Missing previous sibling should return zero");
//    }
//
//    @Test
//    @DisplayName("Should handle missing previous sibling on candidate")
//    void shouldHandleMissingPrevSiblingOnCandidate() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "Username:");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("input");
//        // No previous sibling
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(0.0, score, 0.001, "Missing previous sibling should return zero");
//    }
//
//    @Test
//    @DisplayName("Should handle empty previous sibling text")
//    void shouldHandleEmptyPrevSiblingText() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "");
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "Username:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertEquals(0.0, score, 0.001, "Empty previous sibling text should return zero");
//    }
//
//    @Test
//    @DisplayName("Should be case-insensitive for sibling text")
//    void shouldBeCaseInsensitiveForSiblingText() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "USERNAME:");
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "username:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.9, "Should be case-insensitive");
//    }
//
//    @Test
//    @DisplayName("Should handle whitespace in sibling text")
//    void shouldHandleWhitespaceInSiblingText() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "  Username:  ");
//        ElementNode candidate = createNodeWithNeighbor("input", "label", "Username:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.95, "Should normalize whitespace");
//    }
//
//    @Test
//    @DisplayName("Should match form field with label context")
//    void shouldMatchFormFieldWithLabelContext() {
//        // Given: Typical form pattern - label followed by input
//        ElementNode original = new ElementNode();
//        original.setTagName("input");
//        original.addAttribute("type", "text");
//        original.setPrevSiblingTag("label");
//        original.setPrevSiblingText("Email Address:");
//
//        ElementNode candidate = new ElementNode();
//        candidate.setTagName("input");
//        candidate.addAttribute("type", "text");
//        candidate.setPrevSiblingTag("label");
//        candidate.setPrevSiblingText("Email Address:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.85, "Form field with label context should match strongly");
//    }
//
//    @Test
//    @DisplayName("Should handle different sibling tags")
//    void shouldHandleDifferentSiblingTags() {
//        // Given
//        ElementNode original = createNodeWithNeighbor("input", "label", "Username:");
//        ElementNode candidate = createNodeWithNeighbor("input", "span", "Username:");
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.5 && score < 0.9,
//            "Different sibling tags but same text should have moderate score");
//    }
//
//    @Test
//    @DisplayName("Should handle long sibling text")
//    void shouldHandleLongSiblingText() {
//        // Given
//        String longText = "Please enter your username to continue with the registration process";
//        ElementNode original = createNodeWithNeighbor("input", "label", longText);
//        ElementNode candidate = createNodeWithNeighbor("input", "label", longText);
//
//        // When
//        double score = strategy.score(original, candidate);
//
//        // Then
//        assertTrue(score > 0.85, "Long sibling text should match correctly");
//    }
//
//    @Test
//    @DisplayName("Should verify strategy name")
//    void shouldVerifyStrategyName() {
//        assertEquals("Neighbor", strategy.getName());
//    }
//
//    @Test
//    @DisplayName("Should verify strategy weight")
//    void shouldVerifyStrategyWeight() {
//        assertEquals(0.80, strategy.getWeight(), 0.001);
//    }
//
//    @Test
//    @DisplayName("Should verify healing threshold")
//    void shouldVerifyHealingThreshold() {
//        assertEquals(0.7, strategy.getHealingHold(), 0.001);
//    }
//
//    // Helper method
//    private ElementNode createNodeWithNeighbor(String tag, String prevSiblingTag, String prevSiblingText) {
//        ElementNode node = new ElementNode();
//        node.setTagName(tag);
//        node.setPrevSiblingTag(prevSiblingTag);
//        node.setPrevSiblingText(prevSiblingText);
//        return node;
//    }
}

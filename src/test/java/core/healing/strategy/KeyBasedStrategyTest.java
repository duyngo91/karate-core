package core.healing.strategy;

import core.healing.model.ElementNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KeyBasedStrategy
 * Tests normalized key matching (handles prefixes like btn-, txt_, lbl-)
 */
@DisplayName("KeyBasedStrategy Tests")
class KeyBasedStrategyTest {

    private KeyBasedStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new KeyBasedStrategy();
    }

    @Test
    @DisplayName("Should match keys with different prefixes")
    void shouldMatchKeysWithDifferentPrefixes() {
        // Given: Same semantic meaning, different naming conventions
        ElementNode original = new ElementNode();
        original.addAttribute("id", "btnSubmit");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "submit_button");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.8, "Should recognize 'btnSubmit' and 'submit_button' as similar");
    }

    @Test
    @DisplayName("Should normalize common prefixes")
    void shouldNormalizeCommonPrefixes() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", "txt_username");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "username");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.8, "Should normalize 'txt_' prefix");
    }

    @ParameterizedTest
    @DisplayName("Should handle various naming convention variations")
    @CsvSource({
            "btnLogin, login_button, 0.80",
            "txtUsername, username_input, 0.75",
            "lblPassword, password_label, 0.75",
            "chkRememberMe, remember_me_checkbox, 0.70",
            "ddlCountry, country_dropdown, 0.70"
    })
    void shouldHandleNamingConventionVariations(String originalId, String candidateId, double minExpectedScore) {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", originalId);

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", candidateId);

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score >= minExpectedScore - 0.1,
                String.format("Score %.2f should be >= %.2f for '%s' vs '%s'",
                        score, minExpectedScore, originalId, candidateId));
    }

    @Test
    @DisplayName("Should handle camelCase to snake_case conversion")
    void shouldHandleCamelCaseToSnakeCase() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("name", "submitButton");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("name", "submit_button");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.85, "Should match camelCase with snake_case");
    }

    @Test
    @DisplayName("Should handle uppercase variations")
    void shouldHandleUppercaseVariations() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", "SUBMIT");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "submit");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.9, "Should be case-insensitive");
    }

    @Test
    @DisplayName("Should match formcontrolname variations")
    void shouldMatchFormControlNameVariations() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("formcontrolname", "userEmail");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("formcontrolname", "user_email");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.80, "Should normalize formcontrolname");
    }

    @Test
    @DisplayName("Should handle completely different keys")
    void shouldHandleCompletelyDifferentKeys() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", "loginButton");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "logoutLink");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score < 0.5, "Completely different keys should have low score");
    }

    @Test
    @DisplayName("Should handle missing attributes")
    void shouldHandleMissingAttributes() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", "submit");

        ElementNode candidate = new ElementNode();
        // No id attribute

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertEquals(0.0, score, 0.001, "Missing attributes should return zero score");
    }

    @Test
    @DisplayName("Should prioritize identity attributes")
    void shouldPrioritizeIdentityAttributes() {
        // Given: Both have id and name, id should be prioritized
        ElementNode original = new ElementNode();
        original.addAttribute("id", "submitBtn");
        original.addAttribute("name", "differentName");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "submit_button");
        candidate.addAttribute("name", "anotherName");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.75, "Should prioritize ID attribute matching");
    }

    @Test
    @DisplayName("Should handle data-testid attribute")
    void shouldHandleDataTestId() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("data-testid", "btn-submit");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("data-testid", "submit_button");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.70, "Should normalize data-testid");
    }

    @Test
    @DisplayName("Should handle numeric suffixes")
    void shouldHandleNumericSuffixes() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", "button1");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "button2");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.70, "Should recognize similar base with different numbers");
    }

    @Test
    @DisplayName("Should handle version suffixes")
    void shouldHandleVersionSuffixes() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", "submit-v1");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "submit-v2");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertTrue(score > 0.75, "Should recognize version changes");
    }

    @Test
    @DisplayName("Should verify strategy name")
    void shouldVerifyStrategyName() {
        assertEquals("KeyBased", strategy.getName());
    }

    @Test
    @DisplayName("Should verify strategy weight")
    void shouldVerifyStrategyWeight() {
        assertEquals(0.95, strategy.getWeight(), 0.001);
    }

    @Test
    @DisplayName("Should verify healing threshold")
    void shouldVerifyHealingThreshold() {
        assertEquals(0.7, strategy.getHealingHold(), 0.001);
    }

    @Test
    @DisplayName("Should handle empty strings")
    void shouldHandleEmptyStrings() {
        // Given
        ElementNode original = new ElementNode();
        original.addAttribute("id", "");

        ElementNode candidate = new ElementNode();
        candidate.addAttribute("id", "submit");

        // When
        double score = strategy.score(original, candidate);

        // Then
        assertEquals(0.0, score, 0.001, "Empty strings should return zero score");
    }
}

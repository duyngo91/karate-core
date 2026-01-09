package core.healing.utils;

import java.util.*;

public final class TextNormalizer {

    private TextNormalizer() {
        throw new AssertionError("Cannot instantiate TextNormalizer");
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text.toLowerCase().trim();
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized;
    }

    public static Set<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> tokens = new HashSet<>();
        String[] parts = text.toLowerCase().split("[\\s\\p{Punct}]+");

        for (String part : parts) {
            if (!part.isEmpty()) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    public static double substringScore(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) {
            return 0.0;
        }

        String normalized1 = normalize(s1);
        String normalized2 = normalize(s2);

        if (normalized1.equals(normalized2)) {
            return 1.0;
        }

        if (normalized1.contains(normalized2)) {
            double ratio = (double) normalized2.length() / normalized1.length();
            return 0.7 + (0.3 * ratio);
        }

        if (normalized2.contains(normalized1)) {
            double ratio = (double) normalized1.length() / normalized2.length();
            return 0.7 + (0.3 * ratio);
        }

        return 0.0;
    }

    public static double tokenOverlapScore(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        Set<String> tokens1 = tokenize(text1);
        Set<String> tokens2 = tokenize(text2);

        if (tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);

        Set<String> union = new HashSet<>(tokens1);
        union.addAll(tokens2);

        return (double) intersection.size() / union.size();
    }

    public static double textSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        if (text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        String normalized1 = normalize(text1);
        String normalized2 = normalize(text2);

        if (normalized1.equals(normalized2)) {
            return 1.0;
        }

        double substringScore = substringScore(text1, text2);
        double tokenScore = tokenOverlapScore(text1, text2);
        double semanticScore = SemanticMatcher.semanticSimilarity(text1, text2);

        return Math.max(Math.max(substringScore, tokenScore), semanticScore);
    }
}

package core.healing.utils;

import java.util.*;

/**
 * Advanced semantic matcher using multiple fuzzy matching algorithms.
 */
public class SemanticMatcher {

    // Configurable thresholds
    private static final double HIGH_SIMILARITY_THRESHOLD = 0.85;
    private static final double MEDIUM_SIMILARITY_THRESHOLD = 0.70;
    private static final double LOW_SIMILARITY_THRESHOLD = 0.50;

    /**
     * Calculate semantic similarity using multiple algorithms.
     * Returns weighted combination of all algorithms.
     */
    public static double semanticSimilarity(String value1, String value2) {
        if (value1 == null || value2 == null)
            return 0;
        if (value1.isEmpty() || value2.isEmpty())
            return 0;

        String v1 = normalize(value1);
        String v2 = normalize(value2);

        // Exact match
        if (v1.equals(v2))
            return 1.0;

        // Calculate multiple similarity scores
        double jaroWinkler = jaroWinklerSimilarity(v1, v2);
        double ngram = ngramSimilarity(v1, v2, 2); // bigram
        double lcs = lcsSimilarity(v1, v2);
        double token = tokenSimilarity(v1, v2);
        double substring = substringScore(v1, v2);

        // Weighted combination (tuned for attribute matching)
        double score = 0.30 * jaroWinkler // Best for typos
                + 0.25 * ngram // Good for variations
                + 0.20 * lcs // Good for reordering
                + 0.15 * token // Good for multi-word
                + 0.10 * substring; // Bonus for containment

        return Math.min(1.0, score); // Cap at 1.0
    }

    private static double jaroWinklerSimilarity(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        if (len1 == 0 && len2 == 0)
            return 1.0;
        if (len1 == 0 || len2 == 0)
            return 0.0;

        int matchDistance = Math.max(len1, len2) / 2 - 1;
        if (matchDistance < 1)
            matchDistance = 1;

        boolean[] s1Matches = new boolean[len1];
        boolean[] s2Matches = new boolean[len2];

        int matches = 0;
        int transpositions = 0;

        for (int i = 0; i < len1; i++) {
            int start = Math.max(0, i - matchDistance);
            int end = Math.min(i + matchDistance + 1, len2);

            for (int j = start; j < end; j++) {
                if (s2Matches[j] || s1.charAt(i) != s2.charAt(j))
                    continue;
                s1Matches[i] = true;
                s2Matches[j] = true;
                matches++;
                break;
            }
        }

        if (matches == 0)
            return 0.0;

        int k = 0;
        for (int i = 0; i < len1; i++) {
            if (!s1Matches[i])
                continue;
            while (!s2Matches[k])
                k++;
            if (s1.charAt(i) != s2.charAt(k))
                transpositions++;
            k++;
        }

        double jaro = ((double) matches / len1
                + (double) matches / len2
                + (double) (matches - transpositions / 2.0) / matches) / 3.0;

        int prefixLength = 0;
        for (int i = 0; i < Math.min(4, Math.min(len1, len2)); i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }

        return jaro + (prefixLength * 0.1 * (1.0 - jaro));
    }

    private static double ngramSimilarity(String s1, String s2, int n) {
        if (s1.length() < n || s2.length() < n) {
            return s1.equals(s2) ? 1.0 : 0.0;
        }

        Set<String> ngrams1 = getNgrams(s1, n);
        Set<String> ngrams2 = getNgrams(s2, n);

        if (ngrams1.isEmpty() && ngrams2.isEmpty())
            return 1.0;
        if (ngrams1.isEmpty() || ngrams2.isEmpty())
            return 0.0;

        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        return (double) intersection.size() / union.size();
    }

    private static Set<String> getNgrams(String str, int n) {
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= str.length() - n; i++) {
            ngrams.add(str.substring(i, i + n));
        }
        return ngrams;
    }

    private static double lcsSimilarity(String s1, String s2) {
        int lcsLength = lcs(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        return maxLen > 0 ? (double) lcsLength / maxLen : 0.0;
    }

    private static int lcs(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    private static double tokenSimilarity(String s1, String s2) {
        List<String> tokens1 = tokenize(s1);
        List<String> tokens2 = tokenize(s2);

        if (tokens1.isEmpty() && tokens2.isEmpty())
            return 1.0;
        if (tokens1.isEmpty() || tokens2.isEmpty())
            return 0.0;

        Set<String> set1 = new HashSet<>(tokens1);
        Set<String> set2 = new HashSet<>(tokens2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    private static List<String> tokenize(String str) {
        List<String> tokens = new ArrayList<>();
        String[] parts = str.split("(?=[A-Z])|[_\\-\\s]+");

        for (String part : parts) {
            if (!part.isEmpty()) {
                tokens.add(part.toLowerCase());
            }
        }
        return tokens;
    }

    private static double substringScore(String s1, String s2) {
        if (s1.contains(s2)) {
            return (double) s2.length() / s1.length();
        }
        if (s2.contains(s1)) {
            return (double) s1.length() / s2.length();
        }
        return 0.0;
    }

    private static String normalize(String value) {
        return value.toLowerCase().replaceAll("\\s+", "");
    }
}

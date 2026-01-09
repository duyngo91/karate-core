package core.healing.utils;

import java.util.*;

public final class KeyNormalizer {

    // Common prefixes used in UI element naming
    private static final Set<String> COMMON_PREFIXES = new HashSet<>(Arrays.asList(
            "txt", "input", "inp", "field", "fld",
            "btn", "button", "but",
            "lbl", "label", "lab",
            "chk", "checkbox", "check",
            "rad", "radio",
            "ddl", "dropdown", "select", "sel",
            "div", "span", "p",
            "img", "image", "pic",
            "link", "lnk", "a"));

    // Common suffixes
    private static final Set<String> COMMON_SUFFIXES = new HashSet<>(Arrays.asList(
            "field", "input", "box", "btn", "button", "label", "text", "ctrl", "control"));

    private KeyNormalizer() {
        throw new AssertionError("Cannot instantiate KeyNormalizer");
    }

    public static String normalize(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        List<String> tokens = extractTokens(key);
        tokens = removeCommonAffixes(tokens);
        return String.join("", tokens).toLowerCase();
    }

    public static List<String> extractTokens(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> tokens = new ArrayList<>();
        String[] parts = str.split("(?=[A-Z])|[_\\-\\s]+");

        for (String part : parts) {
            if (!part.isEmpty()) {
                tokens.add(part.toLowerCase());
            }
        }
        return tokens;
    }

    private static List<String> removeCommonAffixes(List<String> tokens) {
        if (tokens.isEmpty()) {
            return tokens;
        }

        List<String> result = new ArrayList<>(tokens);

        if (!result.isEmpty() && COMMON_PREFIXES.contains(result.get(0))) {
            result.remove(0);
        }

        if (!result.isEmpty() && COMMON_SUFFIXES.contains(result.get(result.size() - 1))) {
            result.remove(result.size() - 1);
        }

        return result;
    }

    public static double keySimilarity(String key1, String key2) {
        if (key1 == null || key2 == null) {
            return 0.0;
        }

        String normalized1 = normalize(key1);
        String normalized2 = normalize(key2);

        if (normalized1.equals(normalized2)) {
            return 1.0;
        }

        List<String> tokens1 = extractTokens(key1);
        List<String> tokens2 = extractTokens(key2);

        tokens1 = removeCommonAffixes(tokens1);
        tokens2 = removeCommonAffixes(tokens2);

        Set<String> set1 = new HashSet<>(tokens1);
        Set<String> set2 = new HashSet<>(tokens2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) {
            return 0.0;
        }

        double jaccardSimilarity = (double) intersection.size() / union.size();

        double substringBonus = 0.0;
        if (normalized1.contains(normalized2) || normalized2.contains(normalized1)) {
            substringBonus = 0.2;
        }

        return Math.min(1.0, jaccardSimilarity + substringBonus);
    }
}

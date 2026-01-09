package core.healing.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;
import java.util.Map;

public class SimilarityUtil {
    public static double similarity(String a, String b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty())
            return 0;
        Integer distance = LevenshteinDistance.getDefaultInstance().apply(a, b);
        return 1.0 - (double) distance / Math.max(a.length(), b.length());
    }

    public static double attributeSimilarity(Map<String, String> oldAttrs, Map<String, String> newAttrs) {
        if (oldAttrs == null || newAttrs == null)
            return 0;

        double totalScore = 0;
        int matched = 0;

        for (String key : oldAttrs.keySet()) {
            if (isIgnoredAttribute(key))
                continue;
            if (!newAttrs.containsKey(key))
                continue;

            String oldVal = oldAttrs.get(key);
            String newVal = newAttrs.get(key);

            double sim = SimilarityUtil.similarity(oldVal, newVal);
            totalScore += sim;
            matched++;
        }

        return matched == 0 ? 0 : totalScore / matched;
    }

    private static boolean isIgnoredAttribute(String key) {
        return key.equalsIgnoreCase("style")
                || key.startsWith("on")
                || key.contains("react")
                || key.contains("angular");
    }

    public static double pathSimilarity(String oldPath, String newPath) {
        if (oldPath == null || newPath == null)
            return 0;

        String[] oldParts = oldPath.split("/");
        String[] newParts = newPath.split("/");

        int min = Math.min(oldParts.length, newParts.length);
        int common = 0;

        for (int i = 0; i < min; i++) {
            if (oldParts[i].equalsIgnoreCase(newParts[i])) {
                common++;
            } else {
                break;
            }
        }

        return (double) common / Math.max(oldParts.length, newParts.length);
    }

    public static double depthSimilarity(int oldDepth, int newDepth) {
        int diff = Math.abs(oldDepth - newDepth);
        return 1.0 / (1 + diff);
    }
}

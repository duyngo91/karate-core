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

    public static double structuralSimilarity(String path1, String path2) {
        if (path1 == null || path2 == null || path1.isEmpty() || path2.isEmpty()) {
            return 0;
        }

        String[] parts1 = path1.split(" > ");
        String[] parts2 = path2.split(" > ");

        // Weighted overlap: prioritize matches near the target element
        double score = 0;
        double weight = 1.0;
        double totalWeight = 0;
        
        int p2Idx = 0;
        int matchedCount = 0;
        for (int i = 0; i < parts1.length; i++) {
            totalWeight += weight;
            
            boolean found = false;
            for (int k = p2Idx; k < parts2.length; k++) {
                if (parts1[i].equalsIgnoreCase(parts2[k])) {
                    // Match found!
                    double matchValue = weight;
                    
                    // Direct match bonus: if it's the exact position (k == i or k == p2Idx)
                    if (k == p2Idx) {
                        matchValue *= 1.25; // 25% bonus for keeping perfect sequence
                    }
                    
                    score += matchValue;
                    p2Idx = k + 1; 
                    found = true;
                    matchedCount++;
                    break;
                }
            }
            
            weight *= 0.8; // Decay weight as we go up the tree
        }

        double baseSim = score / totalWeight;
        
        // Final boost: if we found ALL tags from parts1 in the correct order
        if (matchedCount == parts1.length) {
            baseSim *= 1.2; // 20% boost for perfect sequence coverage
        }

        return Math.min(baseSim, 1.0);
    }
}

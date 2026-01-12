package core.healing.strategy;

import core.healing.model.ElementNode;
import core.healing.utils.SimilarityUtil;

import java.util.Objects;

/**
 * Strategy: Structural Match (DOM-aware)
 *
 * Focus:
 * - Same tag (required)
 * - Same form / container
 * - DOM distance
 * - Light text usage (NOT dominant)
 */
public class StructuralStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {

        double score = 0;

        // ===== 0. Original Must have parent =====
        if (original.getStructuralPath() == null || !original.getStructuralPath().contains(">")) {
            return 0;
        }


        // ===== 1. TAG MUST MATCH =====
        if (original.getTagName() == null ||
                candidate.getTagName() == null ||
                !original.getTagName().equalsIgnoreCase(candidate.getTagName())) {
            return 0;
        }

        // ===== 2. SAME FORM / CONTAINER =====
        if (Objects.equals(original.getFormId(), candidate.getFormId())) {
            int indexDistance = Math.abs(
                    original.getDomIndex() - candidate.getDomIndex()
            );
            score += 0.15;
            if (indexDistance == 1) score += 0.15;
            else if (indexDistance == 2) score += 0.08;
        }

        // ===== 3. STRUCTURAL PATH SIMILARITY (Fingerprinting) =====
        if (original.getStructuralPath() != null && candidate.getStructuralPath() != null) {
            double pathSim = SimilarityUtil.structuralSimilarity(
                    original.getStructuralPath(),
                    candidate.getStructuralPath()
            );

            score += pathSim * 0.35;
        }

        // ===== 4. DEPTH & PARENT MATCH (Fallback/Legacy) =====
        if (original.getDepth() > 0 && candidate.getDepth() > 0) {
            double depthSim = SimilarityUtil.depthSimilarity(
                    original.getDepth(),
                    candidate.getDepth()
            );
            score += depthSim * 0.05;
        }

        if (original.getParentTag() != null &&
                original.getParentTag().equalsIgnoreCase(candidate.getParentTag())) {
            score += 0.05;
        }

        // ===== 5. TYPE MATCH (INPUT TYPE) =====
        String oldType = original.getAttribute("type");
        String newType = candidate.getAttribute("type");
        if (oldType != null && oldType.equalsIgnoreCase(newType)) {
            score += 0.1;
        }

        // ===== 6. TEXT / PLACEHOLDER (VERY LIGHT, OPTIONAL) =====
        double textSim = SimilarityUtil.similarity(
                original.getText(),
                candidate.getText()
        );
        score += textSim * 0.05;
        String oldPlaceholder = original.getAttribute("placeholder");
        String newPlaceholder = candidate.getAttribute("placeholder");
        if (oldPlaceholder != null && newPlaceholder != null) {
            double placeholderSim =
                    SimilarityUtil.similarity(oldPlaceholder, newPlaceholder);
            score += placeholderSim * 0.05;
        }
        return Math.min(score, 1.0);
    }

    @Override
    public String getName() {
        return "Structural";
    }

    @Override
    public double getWeight() {
        return 0.4; // 🔥 tăng weight vì đây là backbone strategy
    }
    @Override
    public double getHealingHold() {
        return 0.3;
    }
}

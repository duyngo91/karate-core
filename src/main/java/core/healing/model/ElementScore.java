package core.healing.model;

import core.healing.HealingConfig;
import core.healing.model.ElementNode;
import core.healing.model.HealingResult;
import core.healing.model.StrategyMatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ElementScore {
    public double roleScore = 1.0;

    public final ElementNode element;

    public List<StrategyMatch> matches = new ArrayList<>();

    public int passCount = 0;
    public int totalStrategies = 0;

    public double totalWeightedScore = 0;
    public double bestRawScore = 0;

    private double confidence = -1;

    public ElementScore(ElementNode element) {
        this.element = element;
    }

    /**
     * Confidence ∈ [0,1]
     */
    public double getConfidence() {
        if (confidence >= 0) return confidence;

        double normalizedWeighted =
                totalStrategies == 0 ? 0 :
                        totalWeightedScore / totalStrategies;

        double passRatio =
                totalStrategies == 0 ? 0 :
                        (double) passCount / totalStrategies;

        confidence =
                0.45 * clamp(normalizedWeighted) +
                        0.35 * clamp(bestRawScore) +
                        0.20 * clamp(passRatio);

        // 👇 SCALE CUỐI – KHÔNG GIẾT SCORE
        confidence *= clamp(roleScore);

        return confidence;
    }

    public HealingResult getHealingResult() {
        element.genLocator();
        return new HealingResult(
                element,
                getConfidence(),
                getBestStrategyName()
        );
    }

    private String getBestStrategyName() {
        return matches.stream()
                .max(Comparator.comparingDouble(StrategyMatch::getWeightedScore))
                .map(m -> m.getStrategy().getName())
                .orElse("Unknown");
    }

    private double clamp(double v) {
        return Math.max(0, Math.min(1, v));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n================ ELEMENT SCORE ================\n");

        // Element summary
        sb.append(element);

        sb.append("\nSummary:\n");
        sb.append("  passCount          : ").append(passCount).append("\n");
        sb.append("  confidence       : ").append(getConfidence()).append("\n");
        sb.append("  bestRawScore       : ").append(format(bestRawScore)).append("\n");
        sb.append("  totalWeightedScore : ").append(format(totalWeightedScore)).append("\n");

        sb.append("\nStrategy Breakdown:\n");

        for (StrategyMatch m : matches) {
            sb.append("  - ")
                    .append(m.getStrategy().getName())
                    .append(" | raw=")
                    .append(format(m.getRawScore()))
                    .append(" | weighted=")
                    .append(format(m.getWeightedScore()));

            if (m.getRawScore() >= HealingConfig.HEALING_THRESHOLD) {
                sb.append("  ✔ PASS");
            }

            sb.append("\n");
        }

        sb.append("===============================================\n");
        return sb.toString();
    }
    private String format(double v) {
        return String.format("%.3f", v);
    }

    private String truncate(String s, int max) {
        if (s == null) return "null";
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}

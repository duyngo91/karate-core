package core.healing.model;

import core.healing.HealingConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


import static org.apache.commons.lang3.StringUtils.truncate;

public class ElementScore {
    public ElementNode element;
    public List<StrategyMatch> matches = new ArrayList<>();

    public int passCount;
    public double bestRawScore;
    public double totalWeightedScore;

    public ElementScore(ElementNode element) {
        this.element = element;

    }

    public ElementScore(ElementNode element, List<StrategyMatch> matches, int passCount, double bestRawScore, double totalWeightedScore) {
        this.element = element;
        this.matches = matches;
        this.passCount = passCount;
        this.bestRawScore = bestRawScore;
        this.totalWeightedScore = totalWeightedScore;
    }

    public HealingResult getHealingResult() {
        this.element.genLocator();

        return new HealingResult(
                this.element,
                this.bestRawScore,
                this.matches.stream().sorted(Comparator.comparingDouble(StrategyMatch::getWeightedScore).reversed())
                        .findFirst()
                        .get()
                        .getStrategy()
                        .getName()
        );
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("\n================ ELEMENT SCORE ================\n");

        // Element summary
        sb.append(element);

        sb.append("\nSummary:\n");
        sb.append("  passCount          : ").append(passCount).append("\n");
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


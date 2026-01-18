package core.healing.domain.model;

public class HealingResult {
    private final ElementNode element;
    private final double score;
    private final String strategyUsed;
    private final boolean success;

    // Static failure instance
    public static final HealingResult FAILURE = new HealingResult(null, 0.0, "None");

    public HealingResult(ElementNode element, double score, String strategyUsed) {
        this.element = element;
        this.score = score;
        this.strategyUsed = strategyUsed;
        this.success = element != null;
    }

    public static HealingResult failure() {
        return FAILURE;
    }

    public boolean isSuccess() {
        return success;
    }

    public ElementNode getElement() {
        return element;
    }

    public double getScore() {
        return score;
    }

    public String getStrategyUsed() {
        return strategyUsed;
    }

    @Override
    public String toString() {
        if (!success)
            return "HealingResult[Failed]";
        return String.format("HealingResult[Success, Score=%.2f, Strategy=%s, Element=%s]",
                score, strategyUsed, element);
    }
}

package core.healing.domain.model;

import core.healing.domain.strategy.HealingStrategy;

public class StrategyMatch {
    ElementNode element;
    HealingStrategy strategy;
    double rawScore;
    double weightedScore;

    public StrategyMatch(ElementNode element, HealingStrategy strategy, double rawScore, double weightedScore) {
        this.element = element;
        this.strategy = strategy;
        this.rawScore = rawScore;
        this.weightedScore = weightedScore;
    }

    public ElementNode getElement() {
        return element;
    }

    public void setElement(ElementNode element) {
        this.element = element;
    }

    public HealingStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(HealingStrategy strategy) {
        this.strategy = strategy;
    }

    public double getRawScore() {
        return rawScore;
    }

    public void setRawScore(double rawScore) {
        this.rawScore = rawScore;
    }

    public double getWeightedScore() {
        return weightedScore;
    }

    public void setWeightedScore(double weightedScore) {
        this.weightedScore = weightedScore;
    }
}

package core.healing.domain;

import core.healing.application.port.IHealingDriver;
import core.healing.domain.model.*;
import core.healing.domain.strategy.HealingStrategy;
import core.healing.domain.strategy.VisualHealingStrategy;
import core.healing.infrastructure.config.HealingConfig;
import core.platform.utils.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * Core healing engine that orchestrates the multi-strategy healing process.
 */
public class HealingEngine {

    private final List<HealingStrategy> strategies;
    private final java.util.concurrent.ForkJoinPool customPool;
    private final boolean isParallel;



    public HealingEngine(List<HealingStrategy> strategies, boolean isParallel, int maxThreads) {
        this.strategies = strategies;
        this.isParallel = isParallel;
        this.customPool = isParallel ? new ForkJoinPool(maxThreads) : null;
    }



    public HealingResult heal(IHealingDriver driver, ElementNode original, List<ElementNode> candidates) {
        Map<ElementNode, ElementScore> scoreMap;
        if (isParallel) {
            scoreMap = new java.util.concurrent.ConcurrentHashMap<>();
            try {
                List<ElementNode> finalCandidates = candidates;
                customPool.submit(() ->
                    finalCandidates.parallelStream().forEach(candidate -> {
                        ElementScore elementScore = scoreCandidates(original, candidate);
                        if (!elementScore.matches.isEmpty()) {
                            scoreMap.put(candidate, elementScore);
                        }
                    })
                ).get();
            } catch (Exception e) {
                Logger.error("Parallel healing failed: %s", e.getMessage());
            }
        } else {
            scoreMap = new HashMap<>();
            // ===== PHASE 1: DOM STRATEGIES =====
            for (ElementNode candidate : candidates) {
                ElementScore elementScore = scoreCandidates(original, candidate);

                if (!elementScore.matches.isEmpty()) {
                    scoreMap.put(candidate, elementScore);
                    if (elementScore.getConfidence() > HealingConfig.CONFIDENCE_WEAK) {
                        Logger.debug("[Healing] Potential: %s (Conf: %.2f)",
                                candidate.getTagName(), elementScore.getConfidence());
                    }
                }
            }
        }

        // ===== PHASE 2: CHOOSE BEST BY CONFIDENCE =====
        ElementScore best = null;
        if (!scoreMap.isEmpty()) {
            List<ElementScore> sorted = scoreMap.values()
                    .stream()
                    .sorted(Comparator.comparing(ElementScore::getConfidence, Comparator.reverseOrder())
                            .thenComparing((ElementScore e) -> e.bestRawScore, Comparator.reverseOrder()))
                    .toList();

            // debug ElementScore
            if (HealingConfig.ENABLE_DEBUG_LOGGING) {
                sorted.stream().limit(5).forEach(System.out::println);
            }

            best = sorted.get(0);

            if (best.getConfidence() >= HealingConfig.CONFIDENCE_OK) {
                return best.getHealingResult();
            }
        }

        // ===== PHASE 3: VISUAL FALLBACK =====
        HealingResult visualHealing = tryVisualHealing(driver, original, candidates);
        return visualHealing != null ?
                visualHealing : best != null ?
                best.getHealingResult() : HealingResult.failure();
    }

    private ElementScore scoreCandidates(ElementNode original, ElementNode candidate) {
        ElementScore elementScore = new ElementScore(candidate);

        // 👇 soft role penalty / boost
        elementScore.roleScore = roleCompatibilityScore(original, candidate);

        for (HealingStrategy strategy : strategies) {
            if (strategy instanceof VisualHealingStrategy)
                continue;

            double raw = strategy.score(original, candidate);
            //  raw = 0 || 3 || 5 ?
            if (raw < strategy.getHealingHold())
                continue;

            double weighted = raw * strategy.getWeight();

            elementScore.matches.add(
                    new StrategyMatch(candidate, strategy, raw, weighted));

            elementScore.totalStrategies++;

            if (raw >= HealingConfig.HEALING_THRESHOLD) {
                elementScore.passCount++;
            }

            elementScore.totalWeightedScore += weighted;
            elementScore.bestRawScore = Math.max(elementScore.bestRawScore, raw);
        }
        return elementScore;
    }

    private HealingResult tryVisualHealing(
            IHealingDriver driver,
            ElementNode original,
            List<ElementNode> candidates) {

        Logger.warn("DOM healing failed. Trying visual healing...");

        ElementNode bestVisual = null;
        double bestScore = 0;
        HealingStrategy bestStrategy = null;

        for (HealingStrategy strategy : strategies) {
            if (!(strategy instanceof VisualHealingStrategy)) continue;

            // Inject current driver for lazy capture
            ((VisualHealingStrategy) strategy).setDriver(driver);

            for (ElementNode candidate : candidates) {

                double raw = strategy.score(original, candidate);
                if (raw <= 0) continue;

                // 👇 apply role penalty
                double finalScore = raw * roleCompatibilityScore(original, candidate);

                if (finalScore > bestScore) {
                    bestScore = finalScore;
                    bestVisual = candidate;
                    bestStrategy = strategy;
                }
            }
        }

        if (bestVisual != null && bestScore >= HealingConfig.VISUAL_THRESHOLD) {
            bestVisual.genLocator();
            return new HealingResult(
                    bestVisual,
                    bestScore,
                    bestStrategy.getName()
            );
        }

        return null;
    }


    /**
     * Soft role compatibility ∈ (0,1]
     */
    private double roleCompatibilityScore(ElementNode original, ElementNode candidate) {

        ElementRole o = original.inferRole();
        ElementRole c = candidate.inferRole();

        if (o == c) return 1.0;

        // input ↔ label
        if ((o == ElementRole.INPUT && c == ElementRole.LABEL) ||
                (o == ElementRole.LABEL && c == ElementRole.INPUT))
            return 0.7;

        // button ↔ link
        if ((o == ElementRole.BUTTON && c == ElementRole.LINK) ||
                (o == ElementRole.LINK && c == ElementRole.BUTTON))
            return 0.6;

        // text ↔ label
        if (o == ElementRole.TEXT && c == ElementRole.LABEL)
            return 0.5;

        return 0.2; // very weak but never zero
    }
}

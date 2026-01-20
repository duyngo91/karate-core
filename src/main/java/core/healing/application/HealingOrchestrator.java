package core.healing.application;

import core.healing.application.locator.LocatorMapper;
import core.healing.application.port.*;
import core.healing.domain.HealingEngine;
import core.healing.domain.model.ElementNode;
import core.healing.domain.model.HealingEvent;
import core.healing.domain.model.HealingResult;
import core.intelligence.FailureAnalyzer;
import core.intelligence.DiagnosticReport;
import core.platform.utils.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class HealingOrchestrator {
    private final HealingStore store;
    private final GoldenStateStore goldenStateStore;
    private final HealingEngine engine;
    private final CandidateExtractor extractor;
    private final HealingMonitor monitor;
    private final CandidateProvider candidateProvider;
    private final FailureAnalyzer failureAnalyzer;
    private final VectorStoreAdapter vectorStore;


    public HealingOrchestrator(
            HealingStore store,
            GoldenStateStore goldenStateStore,
            HealingEngine engine,
            CandidateExtractor extractor,
            CandidateProvider candidateProvider,
            HealingMonitor monitor,
            FailureAnalyzer failureAnalyzer,
            VectorStoreAdapter vectorStore) {

        this.store = store;
        this.goldenStateStore = goldenStateStore;
        this.engine = engine;
        this.extractor = extractor;
        this.monitor = monitor;
        this.candidateProvider = candidateProvider;
        this.failureAnalyzer = failureAnalyzer;
        this.vectorStore = vectorStore;
    }

    // ==========================
    // PHASE 1 – FAST HEAL
    // ==========================

    public Optional<String> tryFastHeal(
            String elementId,
            IHealingDriver driver) {

        // 1️⃣ In-memory store
        if(elementId != null && !elementId.isEmpty()){
            String cached = store.get(elementId);
            if (cached != null && driver.exist(cached, 3000)) {
                return Optional.of(cached);
            }

            // 2️⃣ Golden State
            String learned = goldenStateStore.findLearnedLocator(elementId);
            if (learned != null && driver.exist(learned, 3000)) {
                store.save(elementId, learned);
                return Optional.of(learned);
            }
        }

        return Optional.empty();
    }

    // ==========================
    // PHASE 2 – DEEP HEAL
    // ==========================

    public Optional<String> deepHeal(
            String elementId,
            String originalLocator,
            IHealingDriver driver) {

        ElementNode target = extractor.extract(elementId, originalLocator);
        List<ElementNode> candidates = candidateProvider.getCandidates();
        HealingResult result = engine.heal(driver, target, candidates);

        if (!result.isSuccess()) {
            return Optional.empty();
        }

        String healed = result.getElement().getLocator();

        // persist
        store.save(elementId, healed);
        goldenStateStore.recordSuccess(elementId, healed, driver);

        if (vectorStore != null) {
            // In a real case, we'd generate a vector for the 'healed' element
            // For now, let's assume we update the success rate or just upsert the existing one if we had it
            // This is a placeholder for where the 'Learning' happens
            Logger.info("Learning: Upserting successful healed locator to Vector Store");
        }

        monitor.onEvent(
                new HealingEvent(
                        elementId,
                        originalLocator,
                        healed,
                        result.getStrategyUsed(),
                        result.getScore(),
                        true,
                        System.currentTimeMillis()
                )
        );
        return Optional.of(result).map(r -> r.getElement().getLocator());
    }

    // ==========================
    // COMPOSED API
    // ==========================

    public Optional<String> heal(
            String elementId,
            String originalLocator,
            IHealingDriver driver) {

        // ===== PHASE 0: CACHE =====
        Optional<String> fast = tryFastHeal(elementId, driver);
        if (fast.isPresent()) {
            return fast;
        }

        // ===== PHASE 1: ORIGINAL =====
        if (driver.exists(originalLocator)) {
            return originalLocator.describeConstable();
        }

        // ===== PHASE 2: HEALING =====
        return deepHeal(elementId, originalLocator, driver);
    }

    public <T> T executeWithHealing(
            String locator,
            IHealingDriver driver,
            Function<String, T> action
    ) {
        String elementId = LocatorMapper.getInstance().getElementId(locator);

        // Phase 0–1
        Optional<String> fast = tryFastHeal(elementId, driver);
        try {
            return action.apply(fast.orElse(locator));
        } catch (Exception e) {
            // Phase 2–3
            Optional<String> deep = deepHeal(elementId, locator, driver);
            if (deep.isPresent()) {
                return action.apply(deep.get());
            }

            // Phase 4 - Failure Intelligence
            if (failureAnalyzer != null) {
                DiagnosticReport report = failureAnalyzer.analyze(locator, e, driver);
                Logger.error("AI FAILURE DIAGNOSIS: {}", report.getAnalysis());
                Logger.info("AI SUGGESTION: {}", report.getSuggestion());
            }
            
            throw e; // Rethrow original or wrap in intelligent exception
        }
    }

}

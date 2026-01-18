package core.healing.application;

import core.healing.IHealingDriver;
import core.healing.domain.CandidateExtractor;
import core.healing.domain.HealingEngine;
import core.healing.domain.model.HealingEvent;
import core.healing.domain.port.GoldenStateStore;
import core.healing.domain.model.ElementNode;
import core.healing.domain.model.HealingResult;
import core.healing.application.port.HealingMonitor;

import java.util.Optional;

public class HealingOrchestrator {
    private final HealingStore store;
    private final GoldenStateStore goldenStateStore;
    private final HealingEngine engine;
    private final CandidateExtractor extractor;
    private final HealingMonitor monitor;

    public HealingOrchestrator(
            HealingStore store,
            GoldenStateStore goldenStateStore,
            HealingEngine engine,
            CandidateExtractor extractor,
            HealingMonitor monitor) {

        this.store = store;
        this.goldenStateStore = goldenStateStore;
        this.engine = engine;
        this.extractor = extractor;
        this.monitor = monitor;
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
        HealingResult result = engine.heal(driver, target);

        if (!result.isSuccess()) {
            return Optional.empty();
        }

        String healed = result.getElement().getLocator();

        // persist
        store.save(elementId, healed);
        goldenStateStore.recordSuccess(elementId, healed, driver);

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

        // Phase 1
        Optional<String> fast = tryFastHeal(elementId, driver);
        if (fast.isPresent()) {
            return fast;
        }

        // Phase 2
        return deepHeal(elementId, originalLocator, driver);
    }


}

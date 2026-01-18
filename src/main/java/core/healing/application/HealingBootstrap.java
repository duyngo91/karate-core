package core.healing.application;

import core.healing.SelfHealingDriver;
import core.healing.application.port.CandidateProvider;
import core.healing.application.port.IHealingDriver;
import core.healing.domain.DefaultCandidateExtractor;
import core.healing.infrastructure.InMemoryHealingStore;
import core.healing.infrastructure.candidate.JsCandidateProvider;
import core.healing.infrastructure.config.HealingConfig;
import core.healing.runtime.HealingCache;
import core.healing.runtime.HealingRuntime;

public final class HealingBootstrap {

    public static SelfHealingDriver create(IHealingDriver driver) {

        HealingConfig config = HealingConfig.getInstance();

        // DISABLED MODE
        if (!config.isEnabled()) {
            return new SelfHealingDriver(driver, null, false);
        }

        // ENABLED MODE
        HealingRuntime.start();
        HealingRuntime runtime = HealingRuntime.get();

        HealingStore store =
                new InMemoryHealingStore(HealingCache.getInstance());

        CandidateProvider candidateProvider =
                new JsCandidateProvider(driver);

        HealingOrchestrator orchestrator =
                new HealingOrchestrator(
                        store,
                        runtime.goldenStateStore(),
                        runtime.engine(),
                        new DefaultCandidateExtractor(),
                        candidateProvider,
                        runtime.monitor()
                );

        return new SelfHealingDriver(driver, orchestrator, true);
    }
}


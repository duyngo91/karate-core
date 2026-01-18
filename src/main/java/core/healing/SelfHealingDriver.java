package core.healing;

import core.healing.application.HealingOrchestrator;
import core.healing.application.HealingStore;
import core.healing.application.port.HealingMonitor;
import core.healing.domain.DefaultCandidateExtractor;
import core.healing.domain.HealingEngine;
import core.healing.domain.port.GoldenStateStore;
import core.healing.infrastructure.InMemoryHealingStore;
import core.healing.runtime.HealingRuntime;
import core.platform.utils.Logger;

public class SelfHealingDriver {

    private final IHealingDriver driver;
    private final HealingOrchestrator orchestrator;
    private final HealingStore cache;
    private final GoldenStateStore goldenStateStore;

    public SelfHealingDriver(IHealingDriver driver) {
        this.driver = driver;
        goldenStateStore = HealingRuntime.get().goldenStateStore();
        cache = new InMemoryHealingStore(HealingCache.getInstance());
        this.orchestrator = new HealingOrchestrator(
                cache,
                HealingRuntime.get().goldenStateStore(),
                HealingRuntime.get().engine(),
                new DefaultCandidateExtractor(),
                HealingRuntime.get().monitor()
        );
    }

    public String tryFastHeal(String locator) {
        String elementId = LocatorMapper.getInstance().getElementId(locator);
        return orchestrator
                .tryFastHeal(elementId, driver)
                .orElse(locator);
    }

    public String deepHeal(String locator) {
        String elementId = LocatorMapper.getInstance().getElementId(locator);
        return orchestrator
                .deepHeal(elementId, locator, driver)
                .orElse(locator);
    }

    public String resolve(String locator) {

        String elementId = LocatorMapper.getInstance().getElementId(locator);

        return orchestrator
                .heal(elementId, locator, driver)
                .orElse(locator);
    }





}

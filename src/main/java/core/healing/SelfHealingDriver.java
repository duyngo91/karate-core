package core.healing;

import core.healing.application.HealingOrchestrator;
import core.healing.application.HealingStore;
import core.healing.application.locator.LocatorMapper;
import core.healing.application.port.CandidateProvider;
import core.healing.application.port.IHealingDriver;
import core.healing.domain.DefaultCandidateExtractor;
import core.healing.application.port.GoldenStateStore;
import core.healing.infrastructure.InMemoryHealingStore;
import core.healing.infrastructure.candidate.JsCandidateProvider;
import core.healing.runtime.HealingCache;
import core.healing.runtime.HealingRuntime;

public class SelfHealingDriver {

    private final IHealingDriver driver;
    private final HealingOrchestrator orchestrator;
    private final HealingStore cache;
    private final GoldenStateStore goldenStateStore;

    public SelfHealingDriver(IHealingDriver driver) {
        this.driver = driver;
        goldenStateStore = HealingRuntime.get().goldenStateStore();
        cache = new InMemoryHealingStore(HealingCache.getInstance());
        CandidateProvider candidateProvider = new JsCandidateProvider(driver);

        this.orchestrator = new HealingOrchestrator(
                cache,
                goldenStateStore,
                HealingRuntime.get().engine(),
                new DefaultCandidateExtractor(),
                candidateProvider,
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

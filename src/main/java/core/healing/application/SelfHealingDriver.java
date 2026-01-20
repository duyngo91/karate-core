package core.healing.application;

import core.healing.application.port.IHealingDriver;

import java.util.function.Function;

public final class SelfHealingDriver {

    private final IHealingDriver driver;
    private final HealingOrchestrator orchestrator;
    private final boolean enabled;

    public SelfHealingDriver(
            IHealingDriver driver,
            HealingOrchestrator orchestrator,
            boolean enabled
    ) {
        this.driver = driver;
        this.orchestrator = orchestrator;
        this.enabled = enabled;
    }

    public <T> T execute(String locator, Function<String, T> action) {
        if (!enabled) {
            return action.apply(locator); // bypass healing
        }
        return orchestrator.executeWithHealing(locator, driver, action);
    }
}



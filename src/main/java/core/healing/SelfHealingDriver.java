package core.healing;

import com.intuit.karate.driver.Element;
import core.healing.application.HealingOrchestrator;
import core.healing.application.port.IHealingDriver;

import java.util.function.Function;
import java.util.function.Supplier;

public final class SelfHealingDriver {

    private final IHealingDriver driver;
    private final HealingOrchestrator orchestrator;

    public SelfHealingDriver(
            IHealingDriver driver,
            HealingOrchestrator orchestrator
    ) {
        this.driver = driver;
        this.orchestrator = orchestrator;
    }

    public <T> T execute(String locator, Function<String, T> action) {
        return orchestrator.executeWithHealing(locator, driver, action);
    }
}



package core.mcp.command;

import com.intuit.karate.driver.Driver;
import core.mcp.strategy.ValidationStrategy;
import core.platform.mobile.MobileCustom;
import core.platform.web.ChromeCustom;

public abstract class AbstractDriverCommand extends AbstractToolCommand {
    protected Driver driver;

    protected AbstractDriverCommand(Driver driver, ValidationStrategy validationStrategy) {
        this.name = null;
        this.driver = driver;
        this.validationStrategy = validationStrategy;
        this.recordable = true;
    }

    protected Driver getDriver() {
        return driver;
    }

    protected ChromeCustom getChrome() {
        return (ChromeCustom)driver;
    }

    protected MobileCustom getMobile() {
        return (MobileCustom)driver;
    }

    @Override
    public boolean isRecordable() {
        return true;
    }
}

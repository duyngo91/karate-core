package core.mcp.command.mobile;

import com.intuit.karate.driver.Driver;
import core.mcp.command.AbstractDriverCommand;
import core.mcp.strategy.LocatorValidationStrategy;

import java.util.Map;

public class MobileCloseCommand extends AbstractDriverCommand {
    public MobileCloseCommand(Driver driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        getDriver().quit();
        return "Mobile closed";
    }

    

}

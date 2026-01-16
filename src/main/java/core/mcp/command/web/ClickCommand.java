package core.mcp.command.web;

import com.intuit.karate.driver.Driver;
import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;

import java.util.Map;

public class ClickCommand extends AbstractDriverCommand {
    
    public ClickCommand(Driver driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String locator = args.get(ToolNames.LOCATOR).toString();
        getDriver().click(locator);
        return "Clicked: " + locator;
    }

}

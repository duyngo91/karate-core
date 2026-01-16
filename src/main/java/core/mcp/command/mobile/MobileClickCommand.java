package core.mcp.command.mobile;

import com.intuit.karate.driver.Driver;
import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;

import java.util.Map;

public class MobileClickCommand extends AbstractDriverCommand {
    public MobileClickCommand(Driver driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        getDriver().click(args.get(ToolNames.LOCATOR).toString());
        return "Clicked " + args.get(ToolNames.LOCATOR);
    }

}

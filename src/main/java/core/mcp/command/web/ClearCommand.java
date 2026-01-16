package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class ClearCommand extends AbstractDriverCommand {
    public ClearCommand(ChromeCustom driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String locator = args.get(ToolNames.LOCATOR).toString();
        getDriver().clear(locator);
        return "Cleared " + locator;
    }

}

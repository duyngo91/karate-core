package core.mcp.command.mobile;

import com.intuit.karate.driver.Driver;
import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class MouseClickCommand extends AbstractDriverCommand {
    public MouseClickCommand(Driver driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        ArgumentValidator.requireNonEmpty(args, ToolNames.LOCATOR);
        String locator = args.get(ToolNames.LOCATOR).toString();
        ((ChromeCustom)getDriver()).mouseClick(locator);
        return "Mouse clicked " + locator;
    }

}

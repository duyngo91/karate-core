package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;
import core.platform.web.element.droplist.DropList;

import java.util.Map;

public class DropListSelectCommand extends AbstractDriverCommand {
    public DropListSelectCommand(ChromeCustom driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        ArgumentValidator.requireNonNull(args, ToolNames.LOCATOR, ToolNames.VALUE);
        ArgumentValidator.requireNonEmpty(args, ToolNames.LOCATOR);
        DropList dropList = (DropList) getChrome().droplist(args.get(ToolNames.LOCATOR).toString());
        dropList.select(args.get(ToolNames.VALUE).toString());
        return "Droplist selected";
    }

}

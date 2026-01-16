package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;
import core.platform.web.element.droplist.DropList;

import java.util.Map;

public class DropListSearchSelectCommand extends AbstractDriverCommand {
    public DropListSearchSelectCommand(ChromeCustom driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        ArgumentValidator.requireNonNull(args, ToolNames.LOCATOR, ToolNames.SEARCH_VALUE);
        ArgumentValidator.requireNonEmpty(args, ToolNames.LOCATOR);
        DropList dropList = (DropList) getChrome().droplist(args.get(ToolNames.LOCATOR).toString());
        dropList.search(args.get(ToolNames.SEARCH_VALUE).toString());
        return "Droplist search selected";
    }


}

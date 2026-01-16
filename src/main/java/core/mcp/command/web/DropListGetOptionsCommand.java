package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;
import core.platform.web.ChromeCustom;
import core.platform.web.element.droplist.DropList;

import java.util.Map;

public class DropListGetOptionsCommand extends AbstractDriverCommand {
    public DropListGetOptionsCommand(ChromeCustom driver) {
        super(driver, new LocatorValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        DropList dropList = (DropList) getChrome().droplist(args.get(ToolNames.LOCATOR).toString());
        return dropList.selected().toString();
    }

    

}

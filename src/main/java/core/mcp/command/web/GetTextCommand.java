package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.LocatorValidationStrategy;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class GetTextCommand extends AbstractDriverCommand {
    public GetTextCommand(ChromeCustom driver) {
        super(driver, new LocatorValidationStrategy());
    }


    @Override
    public Object execute(Map<String, Object> args) {
        return getChrome().getText(args.get(ToolNames.LOCATOR).toString());
    }

    
}

package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class CloseTabCommand extends AbstractDriverCommand {

    public CloseTabCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonEmpty(args, ToolNames.TITLE));
    }

    @Override
    public Object execute(Map<String, Object> args) {
        getChrome().tab().closeByTitle(args.get(ToolNames.TITLE).toString());
        return "Current tab closed";
    }
}

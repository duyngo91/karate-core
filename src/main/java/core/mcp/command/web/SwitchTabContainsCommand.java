package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class SwitchTabContainsCommand extends AbstractDriverCommand {
    public SwitchTabContainsCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonNull(args, ToolNames.TITLE));
    }

    @Override
    public Object execute(Map<String, Object> args) {
        getChrome().tab().switchToTitleContains(args.get(ToolNames.TITLE).toString());
        return "Switched tab contains";
    }

}

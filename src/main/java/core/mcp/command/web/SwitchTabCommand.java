package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class SwitchTabCommand extends AbstractDriverCommand {

    public SwitchTabCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonNull(args, ToolNames.TITLE));
    }

    @Override
    public Object execute(Map<String, Object> args) {

        getChrome().tab().switchToTitle(args.get(ToolNames.TITLE).toString());
        return "Switched tab";
    }


}

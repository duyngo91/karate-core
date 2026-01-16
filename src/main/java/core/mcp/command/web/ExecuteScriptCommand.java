package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class ExecuteScriptCommand extends AbstractDriverCommand {
    public ExecuteScriptCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonNull(args, ToolNames.SCRIPT));
    }
    @Override
    public Object execute(Map<String, Object> args) {
        return getDriver().script(args.get(ToolNames.SCRIPT).toString()).toString();
    }

    

}

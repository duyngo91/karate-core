package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class CloseBrowserCommand extends AbstractDriverCommand {

    public CloseBrowserCommand(ChromeCustom driver) {
        super(driver, null);
    }

    @Override
    public Object execute(Map<String, Object> args) {
        getDriver().quit();
        return "Browser closed";
    }

    


}

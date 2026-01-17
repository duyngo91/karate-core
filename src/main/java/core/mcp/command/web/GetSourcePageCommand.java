package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class GetSourcePageCommand extends AbstractDriverCommand {
    public GetSourcePageCommand(ChromeCustom driver) {
        super(driver, null);
    }
    @Override
    public Object execute(Map<String, Object> args) {
        return getChrome().getPageSource();
    }

    @Override
    public boolean isRecordable() {
        return false;
    }


}

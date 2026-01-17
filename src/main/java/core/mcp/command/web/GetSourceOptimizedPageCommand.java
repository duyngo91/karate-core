package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class GetSourceOptimizedPageCommand extends AbstractDriverCommand {
    public GetSourceOptimizedPageCommand(ChromeCustom driver) {
        super(driver, null);
    }
    @Override
    public Object execute(Map<String, Object> args) {
        return getChrome().getOptimizedPageSource();
    }

    @Override
    public boolean isRecordable() {
        return false;
    }

}

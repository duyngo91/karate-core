package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.config.McpConfig;
import core.mcp.constant.ToolNames;
import core.platform.manager.DriverManager;
import core.platform.web.ChromeCustom;

import java.util.HashMap;
import java.util.Map;

public class InitBrowserCommand extends AbstractDriverCommand {
    public InitBrowserCommand() {
        super(null, null);
    }
    @Override
    public Object execute(Map<String, Object> args) {
        String session = args.getOrDefault(ToolNames.SESSION, McpConfig.getInstance().getDefaultSession()).toString();
        if (!DriverManager.hasDriver(session)) {
            DriverManager.addDriver(session, ChromeCustom.start(new HashMap<>()));
        }
        return "Browser initialized: " + session;
    }

}

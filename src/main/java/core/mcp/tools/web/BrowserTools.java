package core.mcp.tools.web;

import core.mcp.constant.ToolNames;
import core.mcp.record.ScriptRecorder;
import core.mcp.tools.BaseToolExecutor;
import core.platform.manager.DriverManager;
import core.platform.web.ChromeCustom;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.*;

import static core.mcp.KarateMCPServer.createTool;

public class BrowserTools extends BaseToolExecutor {

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();

        tools.add(createTool(ToolNames.INIT_BROWSER, "Init Chrome browser", (ex, args) ->
                new BrowserTools().execute(ToolNames.INIT_BROWSER, args, a -> {
                    String session = a.getOrDefault("session", DEFAULT_SESSION).toString();
                    if (!DriverManager.hasDriver(session)) {
                        DriverManager.addDriver(session, ChromeCustom.start(new HashMap<>()));
                    }
                    ScriptRecorder.getInstance().start();
                    return "Browser initialized: " + session;
                }, true)
        ));

        tools.add(createTool(ToolNames.NAVIGATE, "Navigate to URL", (ex, args) ->
                new BrowserTools().execute(ToolNames.NAVIGATE, args, a -> {
                    getWebDriver(a).setUrl(a.get(ToolNames.URL).toString());
                    return "Navigated to " + a.get(ToolNames.URL);
                }, true)
        ));

        tools.add(createTool(ToolNames.GET_PAGE_TITLE, "Get title", (ex, args) ->
                new BrowserTools().execute(ToolNames.GET_PAGE_TITLE, args,
                        a -> getWebDriver(a).getTitle(), false)
        ));

        tools.add(createTool(ToolNames.CLOSE, "Close browser", (ex, args) ->
                new BrowserTools().execute(ToolNames.CLOSE, args, a -> {
                    getWebDriver(a).quit();
                    ScriptRecorder.getInstance().stop();
                    return "Browser closed";
                }, false)
        ));


        tools.add(createTool(ToolNames.EXECUTE_SCRIPT, "Execute JS script", (ex, args) ->
                new BrowserTools().execute(ToolNames.EXECUTE_SCRIPT, args, a ->
                                getWebDriver(a).script(a.get("script").toString()).toString(),
                        false
                )
        ));

        return tools;
    }


}

package core.mcp.tools.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.mcp.constant.ToolNames;
import core.mcp.record.ScriptRecorder;
import core.mcp.tools.BaseToolExecutor;
import core.platform.manager.DriverManager;
import core.platform.web.ChromeCustom;
import core.platform.web.element.droplist.DropList;
import core.platform.web.element.table.Table;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.mcp.KarateMCPServer.createTool;

public class TabTools extends BaseToolExecutor {

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();

        tools.add(createTool(ToolNames.OPEN_NEW_TAB, "Open new tab", (ex, args) ->
                new TabTools().execute(ToolNames.OPEN_NEW_TAB, args, a -> {
                    getWebDriver(a).tab().openNewTab(a.get(ToolNames.URL).toString());
                    return "New tab opened";
                }, true)
        ));

        tools.add(createTool(ToolNames.SWITCH_TAB, "Switch tab by title", (ex, args) ->
                new TabTools().execute(ToolNames.SWITCH_TAB, args, a -> {
                    getWebDriver(a).tab().switchToTitle(a.get(ToolNames.TITLE).toString());
                    return "Switched tab";
                }, true)
        ));

        tools.add(createTool(ToolNames.SWITCH_TAB_CONTAINS, "Switch tab contains", (ex, args) ->
                new TabTools().execute(ToolNames.SWITCH_TAB_CONTAINS, args, a -> {
                    getWebDriver(a).tab().switchToTitleContains(a.get(ToolNames.TITLE).toString());
                    return "Switched tab contains";
                }, true)
        ));

        tools.add(createTool(ToolNames.GET_TABS, "Get all tabs", (ex, args) ->
                new TabTools().execute(ToolNames.GET_TABS, args,
                        a -> {
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            return gson.toJson(getWebDriver(a).tab().getTabs());
                        },
                        false
                )
        ));

        tools.add(createTool(ToolNames.CLOSE_TAB, "Close tab by title", (ex, args) ->
                new TabTools().execute(ToolNames.CLOSE_TAB, args, a -> {
                    getWebDriver(a).tab().closeByTitle(a.get(ToolNames.TITLE).toString());
                    return "Current tab closed";
                }, true)
        ));

        return tools;
    }


}

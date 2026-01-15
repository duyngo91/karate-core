package core.mcp.tools.web;

import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.platform.web.element.droplist.DropList;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static core.mcp.KarateMCPServer.createTool;

public class DropListTools extends BaseToolExecutor {

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();


        tools.add(createTool(ToolNames.DROPLIST_SELECT, "Select droplist", (ex, args) ->
                new DropListTools().execute(ToolNames.DROPLIST_SELECT, args, a -> {
                    getDropList(a).select(a.get("value").toString());
                    return "Droplist selected";
                }, true)
        ));

        tools.add(createTool(ToolNames.DROPLIST_SELECT_CONTAINS, "Select droplist contains", (ex, args) ->
                new DropListTools().execute(ToolNames.DROPLIST_SELECT_CONTAINS, args, a -> {
                    getDropList(a).selectTextContains(a.get("value").toString());
                    return "Droplist select contains";
                }, true)
        ));

        tools.add(createTool(ToolNames.DROPLIST_SEARCH_SELECT, "Search & select droplist", (ex, args) ->
                new DropListTools().execute(ToolNames.DROPLIST_SEARCH_SELECT, args, a -> {
                    getDropList(a).search(a.get("search_value").toString());
                    return "Droplist search selected";
                }, true)
        ));

        tools.add(createTool(ToolNames.DROPLIST_GET_OPTIONS, "Get droplist options", (ex, args) ->
                new DropListTools().execute(ToolNames.DROPLIST_GET_OPTIONS, args, a ->
                                getDropList(a).selected().toString(),
                        false
                )
        ));

        return tools;
    }

    private static DropList getDropList(Map<String, Object> args) {
        return (DropList) getWebDriver(args).droplist(args.get(ToolNames.LOCATOR).toString());
    }

}

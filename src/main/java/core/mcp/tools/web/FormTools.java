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

public class FormTools extends BaseToolExecutor {

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();

        tools.add(createTool(ToolNames.CLICK, "Click element", (ex, args) ->
                new FormTools().execute(ToolNames.CLICK, args, a -> {
                    getWebDriver(a).click(a.get(ToolNames.LOCATOR).toString());
                    return "Clicked " + a.get(ToolNames.LOCATOR);
                }, true)
        ));

        tools.add(createTool(ToolNames.MOUSE_CLICK, "Mouse click element", (ex, args) ->
                new FormTools().execute(ToolNames.MOUSE_CLICK, args, a -> {
                    getWebDriver(a).mouseClick(a.get(ToolNames.LOCATOR).toString());
                    return "Mouse clicked " + a.get(ToolNames.LOCATOR);
                }, true)
        ));

        tools.add(createTool(ToolNames.INPUT, "Input text", (ex, args) ->
                new FormTools().execute(ToolNames.INPUT, args, a -> {
                    getWebDriver(a).input(
                            a.get(ToolNames.LOCATOR).toString(),
                            a.get("text").toString()
                    );
                    return "Input text into " + a.get(ToolNames.LOCATOR);
                }, true)
        ));

        tools.add(createTool(ToolNames.CLEAR, "Clear input", (ex, args) ->
                new FormTools().execute(ToolNames.CLEAR, args, a -> {
                    getWebDriver(a).clear(a.get(ToolNames.LOCATOR).toString());
                    return "Cleared " + a.get(ToolNames.LOCATOR);
                }, true)
        ));

        tools.add(createTool(ToolNames.GET_TEXT, "Get text", (ex, args) ->
                new FormTools().execute(ToolNames.GET_TEXT, args, a ->
                                getWebDriver(a).getText(a.get(ToolNames.LOCATOR).toString()),
                        false
                )
        ));


        return tools;
    }

}

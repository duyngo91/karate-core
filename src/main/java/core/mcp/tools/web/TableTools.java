package core.mcp.tools.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.mcp.constant.ToolNames;
import core.mcp.record.ScriptRecorder;
import core.mcp.tools.BaseToolExecutor;
import core.platform.manager.DriverManager;
import core.platform.web.ChromeCustom;
import core.platform.web.element.table.Table;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.mcp.KarateMCPServer.createTool;

public class TableTools extends BaseToolExecutor {

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();

        tools.add(createTool(ToolNames.TABLE_GET_DATA, "Get table data", (ex, args) ->
                new TableTools().execute(ToolNames.TABLE_GET_DATA, args, a -> {
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            return gson.toJson(getTable(args).getData());
                        },
                        false
                )
        ));


        return tools;
    }

    private static Table getTable(Map<String, Object> args) {
        return (Table) getWebDriver(args).table(args.get("header_locator").toString());
    }
}

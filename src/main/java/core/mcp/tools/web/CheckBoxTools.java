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

public class CheckBoxTools extends BaseToolExecutor {

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();

        tools.add(createTool(ToolNames.CHECKBOX_IS_CHECKED, "Check checkbox status", (ex, args) ->
                new CheckBoxTools().execute(ToolNames.CHECKBOX_IS_CHECKED, args, a ->
                                getWebDriver(a).checked(a.get(ToolNames.LOCATOR).toString()).toString(),
                        false
                )
        ));

        tools.add(createTool(ToolNames.CHECKBOX_SET, "Set checkbox", (ex, args) ->
                new CheckBoxTools().execute(ToolNames.CHECKBOX_SET, args, a -> {
                    getWebDriver(a).setCheckBox(
                            a.get(ToolNames.LOCATOR).toString(),
                            Boolean.parseBoolean(a.get("checked").toString())
                    );
                    return "Checkbox set";
                }, true)
        ));


        return tools;
    }

}

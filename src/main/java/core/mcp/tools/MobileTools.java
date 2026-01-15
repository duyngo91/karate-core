package core.mcp.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.http.HttpClientFactory;
import core.mcp.constant.ToolNames;
import core.mcp.record.ScriptRecorder;
import core.mcp.tools.web.BrowserTools;
import core.platform.manager.DriverManager;
import core.platform.mobile.AndroidCustom;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.lang.reflect.Type;
import java.util.*;

import static core.mcp.KarateMCPServer.createTool;

public class MobileTools extends BaseToolExecutor {


    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();

        tools.add(createTool("connect_android", "Connect Android device", (ex, args) ->
                new MobileTools().execute("connect_android", args, a -> {
                    String capabilities = (String) args.get("info");
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, Object>>() {}.getType();
                    Map<String, Object> map = gson.fromJson(capabilities, type);
                    FeatureRuntime featureRuntime = FeatureRuntime.forTempUse(HttpClientFactory.DEFAULT);
                    ScenarioRuntime scenarioRuntime = featureRuntime.scenarios.next();


                    String session = a.getOrDefault("session", DEFAULT_SESSION).toString();
                    if (!DriverManager.hasDriver(session)) {
                        DriverManager.addDriver(session, AndroidCustom.start(map, scenarioRuntime));
                    }
                    ScriptRecorder.getInstance().start();

                    return "Android connected";
                }, true)
        ));

        tools.add(createTool(ToolNames.CLICK, "Click element", (ex, args) ->
                new BrowserTools().execute(ToolNames.CLICK, args, a -> {
                    getWebDriver(a).click(a.get(ToolNames.LOCATOR).toString());
                    return "Clicked " + a.get(ToolNames.LOCATOR);
                }, true)
        ));

        tools.add(createTool("mobile_click", "Click mobile element", (ex, args) ->
                new MobileTools().execute("mobile_click", args, a -> {
                    getMobileDriver(a).click(a.get(ToolNames.LOCATOR).toString());
                    return "Clicked " + a.get(ToolNames.LOCATOR);
                }, true)
        ));

        tools.add(createTool("mobile_close", "Close mobile", (ex, args) ->
                new MobileTools().execute("mobile_close", args, a -> {
                    getMobileDriver(a).quit();
                    return "Mobile closed";
                }, false)
        ));

        return tools;
    }
}

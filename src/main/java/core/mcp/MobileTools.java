package core.mcp;

import com.google.gson.Gson;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.http.HttpClientFactory;
import com.linecorp.armeria.internal.shaded.guava.reflect.TypeToken;
import core.mcp.recorder.ScriptRecorder;
import core.platform.mobile.AndroidCustom;
import core.platform.mobile.MobileCustom;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.lang.reflect.Type;
import java.util.List;
import java.util.*;

import static core.mcp.KarateMCPServer.createTool;

public class MobileTools {
    public static MobileCustom mobileDriver;
    public static final ScriptRecorder recorder = ScriptRecorder.getInstance();

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();
        addMobileTools(tools);
        return tools;
    }

    // ==================== TOOLS ====================
    private static void addMobileTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("connect_android", "Initialize and connect to android device", (exchange, args) -> {
            try {
                String capabilities = (String) args.get("capabilities");
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> map = gson.fromJson(capabilities, type);
                FeatureRuntime featureRuntime = FeatureRuntime.forTempUse(HttpClientFactory.DEFAULT);
                ScenarioRuntime scenarioRuntime = featureRuntime.scenarios.next();
                mobileDriver = AndroidCustom.start(map, scenarioRuntime, "android_test");
                if (!recorder.isRecording()) recorder.startRecording();
                recorder.recordStep("connect_android", args);
                return ToolHandler.success("Connected to android device : " + map);
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("mobile_click", "click on element", (exchange, args) -> {
            if (mobileDriver == null) return ToolHandler.success("Driver: null");
            try {
                mobileDriver.click(args.get("locator").toString());
                return ToolHandler.success(String.format("Click: " + args.get("locator")));
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("mobile_input", "Input text to element", (exchange, args) -> {
            try {
                String locator = args.get("locator").toString();
                String text = args.get("text").toString();
                mobileDriver.input(locator, text);
                return ToolHandler.success("Scrolled to: " + locator);
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("mobile_swipe", "swipe gesture", (exchange, args) -> {
            try {
                int fromX = Integer.parseInt(args.get("fromX").toString());
                int fromY = Integer.parseInt(args.get("fromY").toString());
                int toX = Integer.parseInt(args.get("toX").toString());
                int toY = Integer.parseInt(args.get("toY").toString());
                mobileDriver.swipeV2(fromX, fromY, toX, toY);
                return ToolHandler.success("swipe complected");
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("mobile_scroll_to_element", "Scroll to find element", (exchange, args) -> {
            try {
                String locator = args.get("locator").toString();
                mobileDriver.scrollDownToView(locator);
                return ToolHandler.success("Scrolled to: " + locator);
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("mobile_close", "close mobile driver", (exchange, args) -> {
            try {
                if(mobileDriver != null){
                    mobileDriver.quit();
                    mobileDriver = null;
                }
                return ToolHandler.success("Mobile driver closed");
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));


        tools.add(createTool("mobile_get_page_source", "Get mobile page source", (exchange, args) -> {
            try {
                return ToolHandler.success(mobileDriver.getPageSource());
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("mobile_get_all_elements", "Get all UI Element with locators", (exchange, args) -> {
            try {
                List<Map<String, Object>> elements = mobileDriver.getAllElementsInfo();
                return ToolHandler.success("Found " + elements.size() + " elements: " + elements.toString());
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));
    }






}
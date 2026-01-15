package core.mcp.tools;

import com.intuit.karate.driver.Driver;
import core.mcp.record.ScriptRecorder;
import core.platform.manager.DriverManager;
import core.platform.mobile.MobileCustom;
import core.platform.web.ChromeCustom;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseToolExecutor {
    public static final String DEFAULT_SESSION = "mcp_session";
    protected final ScriptRecorder recorder = ScriptRecorder.getInstance();

    protected static Driver getDriver(Map<String, Object> args) {
        String session = args.getOrDefault("session", DEFAULT_SESSION).toString();
        Driver driver = DriverManager.getDriver(session);
        if (driver == null) {
            throw new IllegalStateException("Browser not initialized for session: " + session);
        }
        return driver;
    }

    protected static ChromeCustom getWebDriver(Map<String, Object> args) {
        return (ChromeCustom) getDriver(args);
    }

    protected static MobileCustom getMobileDriver(Map<String, Object> args) {
        return (MobileCustom) getDriver(args);
    }

    protected McpSchema.CallToolResult execute(
            String toolName,
            Map<String, Object> args,
            Function<Map<String, Object>, String> action,
            boolean record
    ) {
        try {
            String result = action.apply(args);
            if (record && recorder.isRecording()) {
                recorder.record(toolName, args);
            }
            return success(result);
        } catch (Exception e) {
            return error(e);
        }
    }


    protected McpSchema.CallToolResult success(String msg) {
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(msg)), false);
    }

    protected McpSchema.CallToolResult error(Exception e) {
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
    }
}

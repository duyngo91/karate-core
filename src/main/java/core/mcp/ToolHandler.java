package core.mcp;

import core.mcp.recorder.ScriptRecorder;
import core.platform.web.ChromeCustom;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ToolHandler {
    private final ChromeCustom driver;
    private final ScriptRecorder recorder;

    public ToolHandler(ChromeCustom driver, ScriptRecorder recorder) {
        this.driver = driver;
        this.recorder = recorder;
    }

    public McpSchema.CallToolResult execute(String toolName, Map<String, Object> args, 
                                           Function<Map<String, Object>, String> action, 
                                           boolean recordStep) {
        try {
            if (driver == null) throw new RuntimeException("Browser not initialized");
            String result = action.apply(args);
            if (recordStep) recorder.recordStep(toolName, args);
            return success(result);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    public McpSchema.CallToolResult executeWithoutDriver(Function<Map<String, Object>, String> action) {
        try {
            return success(action.apply(null));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    public static McpSchema.CallToolResult success(String message) {
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(message)), false);
    }

    public static McpSchema.CallToolResult error(String message) {
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + message)), true);
    }
}

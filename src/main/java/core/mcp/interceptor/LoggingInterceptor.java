package core.mcp.interceptor;

import java.util.Map;

public class LoggingInterceptor implements ToolInterceptor {
    
    @Override
    public void before(String toolName, Map<String, Object> args) {
        System.out.println("[MCP] Executing: " + toolName + " with args: " + args);
    }
    
    @Override
    public void after(String toolName, String result, long durationMs) {
        System.out.println("[MCP] Completed: " + toolName + " in " + durationMs + "ms");
    }
    
    @Override
    public void onError(String toolName, Exception e, long durationMs) {
        System.err.println("[MCP] Failed: " + toolName + " after " + durationMs + "ms - " + e.getMessage());
    }
}

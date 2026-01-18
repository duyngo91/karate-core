package core.mcp.hooks;

import java.util.Map;

public interface ToolHook {
    default void before(String toolName, Map<String, Object> args) {}
    
    default void after(String toolName, String result, long durationMs) {}
    
    default void onError(String toolName, Exception e, long durationMs) {}
}

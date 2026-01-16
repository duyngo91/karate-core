package core.mcp.interceptor;

import java.util.Map;

public interface ToolInterceptor {
    default void before(String toolName, Map<String, Object> args) {}
    
    default void after(String toolName, String result, long durationMs) {}
    
    default void onError(String toolName, Exception e, long durationMs) {}
}

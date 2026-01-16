package core.mcp.observer;

import java.util.Map;

public interface ToolExecutionListener {
    void onToolExecuted(String tool, Map<String, Object> args, String result);
}

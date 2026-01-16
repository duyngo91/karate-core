package core.mcp.command;

import java.util.Map;

public interface ToolCommand {
    Object execute(Map<String, Object> args);
    void validate(Map<String, Object> args);
    boolean isRecordable();
}

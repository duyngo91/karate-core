package core.mcp.tools.registry;

import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public interface ToolProvider {
    List<McpServerFeatures.SyncToolSpecification> getTools();
    String getCategory();
    default int getPriority() { return 0; }
}

package core.mcp.tools.registry;

import core.mcp.schema.SchemaLoader;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;
import java.util.function.BiFunction;

public class ToolFactory {
    
    public static McpServerFeatures.SyncToolSpecification create(
            String name, 
            String description,
            BiFunction<McpSyncServerExchange, Map<String, Object>, McpSchema.CallToolResult> handler) {
        return new McpServerFeatures.SyncToolSpecification(
                new McpSchema.Tool(name, description, SchemaLoader.getSchema(name)), 
                handler
        );
    }
}

package core.mcp;

import core.mcp.schema.SchemaLoader;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class KarateMCPServer {
    public static void main(String[] args) {
        StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
        List<McpServerFeatures.SyncToolSpecification> syncToolSpecification = getTools();

        McpSyncServer syncServer = McpServer.sync(transportProvider)
                .serverInfo("karate-browser", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .tools(syncToolSpecification)
                .build();

        try {
            // Keep the server alive
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("Server interrupted: " + e.getMessage());
        }
    }

    private static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();
        tools.addAll(WebTools.getTools());
        tools.addAll(MobileTools.getTools());
        return tools;
    }

    public static McpServerFeatures.SyncToolSpecification createTool(String name, String description,
                                                                      BiFunction<McpSyncServerExchange, Map<String, Object>, McpSchema.CallToolResult> handler) {
        return new McpServerFeatures.SyncToolSpecification(
                new McpSchema.Tool(name, description, SchemaLoader.getSchema(name)), handler);
    }
}
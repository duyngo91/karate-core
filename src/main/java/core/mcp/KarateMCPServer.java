package core.mcp;

import core.mcp.config.McpConfig;
import core.mcp.interceptor.LoggingInterceptor;
import core.mcp.interceptor.MetricsInterceptor;
import core.mcp.observer.RecordingListener;
import core.mcp.schema.SchemaLoader;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolRegistry;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

public class KarateMCPServer {
    private static final MetricsInterceptor metricsInterceptor = new MetricsInterceptor();

    public static void main(String[] args) {
        McpConfig config = McpConfig.getInstance();
        
        StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
        List<McpServerFeatures.SyncToolSpecification> syncToolSpecification = getTools();

        McpSyncServer syncServer = McpServer.sync(transportProvider)
                .serverInfo("karate-browser", config.getServerVersion())
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .tools(syncToolSpecification)
                .build();

        // Print metrics on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n=== MCP Metrics ===");
            metricsInterceptor.getStats().forEach((tool, stats) -> 
                System.out.println(tool + ": " + stats)
            );
        }));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("Server interrupted: " + e.getMessage());
        }
    }

    private static List<McpServerFeatures.SyncToolSpecification> getTools() {
        McpConfig config = McpConfig.getInstance();
        ToolRegistry registry = ToolRegistry.getInstance();
        
        // Register interceptors
        if (config.isLoggingEnabled()) {
            BaseToolExecutor.registerGlobalInterceptor(new LoggingInterceptor());
        }
        if (config.isMetricsEnabled()) {
            BaseToolExecutor.registerGlobalInterceptor(metricsInterceptor);
        }
        
        // Register listeners (Observer Pattern)
        BaseToolExecutor.registerGlobalListener(new RecordingListener());
        
        // Auto-register tools
        registry.autoRegisterAllTools();
        return registry.getAllTools();
    }
}
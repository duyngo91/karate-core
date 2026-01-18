package core.mcp;

import core.mcp.config.McpConfig;
import core.mcp.hooks.LoggingHook;
import core.mcp.hooks.MetricsHook;
import core.mcp.observer.RecordingListener;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolRegistry;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

public class KarateMCPServer {
    private static final MetricsHook metricsHook = new MetricsHook();

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
            metricsHook.getStats().forEach((tool, stats) ->
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
            BaseToolExecutor.registerGlobalInterceptor(new LoggingHook());
        }
        if (config.isMetricsEnabled()) {
            BaseToolExecutor.registerGlobalInterceptor(metricsHook);
        }
        
        // Register listeners (Observer Pattern)
        BaseToolExecutor.registerGlobalListener(new RecordingListener());
        
        // Auto-register tools
        registry.autoRegisterAllTools();
        return registry.getAllTools();
    }
}
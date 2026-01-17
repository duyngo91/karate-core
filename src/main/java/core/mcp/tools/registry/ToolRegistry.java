package core.mcp.tools.registry;

import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ToolRegistry {
    private static final ToolRegistry INSTANCE = new ToolRegistry();
    private final Map<String, McpServerFeatures.SyncToolSpecification> tools = new ConcurrentHashMap<>();

    private ToolRegistry() {}

    public static ToolRegistry getInstance() {
        return INSTANCE;
    }

    public void register(ToolProvider provider) {
        provider.getTools().forEach(tool -> 
            tools.put(tool.tool().name(), tool)
        );
    }

    public void register(String name, McpServerFeatures.SyncToolSpecification tool) {
        tools.put(name, tool);
    }

    public List<McpServerFeatures.SyncToolSpecification> getAllTools() {
        return new ArrayList<>(tools.values());
    }

    public Optional<McpServerFeatures.SyncToolSpecification> getTool(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    public void autoRegisterAllTools() {
        ServiceLoader<ToolProvider> loader = ServiceLoader.load(ToolProvider.class);
        for (ToolProvider provider : loader) {
            try {
                INSTANCE.register(provider);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }



    public void clear() {
        tools.clear();
    }
}

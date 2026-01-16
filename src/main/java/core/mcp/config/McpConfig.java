package core.mcp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class McpConfig {
    private static final McpConfig INSTANCE = new McpConfig();
    private final Properties props = new Properties();

    private McpConfig() {
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream is = getClass().getResourceAsStream("/mcp.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    public static McpConfig getInstance() {
        return INSTANCE;
    }

    public String getDefaultSession() {
        return props.getProperty("mcp.session.default", "mcp_session");
    }

    public int getDefaultTimeout() {
        return Integer.parseInt(props.getProperty("mcp.timeout.default", "30000"));
    }

    public int getMaxRetries() {
        return Integer.parseInt(props.getProperty("mcp.retry.max", "3"));
    }

    public boolean isLoggingEnabled() {
        return Boolean.parseBoolean(props.getProperty("mcp.logging.enabled", "true"));
    }

    public boolean isMetricsEnabled() {
        return Boolean.parseBoolean(props.getProperty("mcp.metrics.enabled", "false"));
    }

    public String getServerVersion() {
        return props.getProperty("mcp.server.version", "1.0.0");
    }
}

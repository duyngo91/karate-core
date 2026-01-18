package core.mcp.tools;

import com.intuit.karate.driver.Driver;
import core.mcp.command.AbstractDriverCommand;
import core.mcp.config.McpConfig;
import core.mcp.hooks.ToolHook;
import core.mcp.observer.ToolExecutionListener;
import core.mcp.tools.registry.ToolBuilder;
import core.mcp.validation.ArgumentValidator;
import core.platform.manager.DriverManager;
import core.platform.mobile.MobileCustom;
import core.platform.web.ChromeCustom;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseToolExecutor {
    protected final McpConfig config = McpConfig.getInstance();
    protected final List<ToolHook> hooks = new ArrayList<>();
    protected final List<ToolExecutionListener> listeners = new ArrayList<>();
    private static final List<ToolHook> globalHooks = new ArrayList<>();
    private static final List<ToolExecutionListener> globalListeners = new ArrayList<>();

    public BaseToolExecutor() {
        hooks.addAll(globalHooks);
        listeners.addAll(globalListeners);
    }

    public static void registerGlobalInterceptor(ToolHook interceptor) {
        globalHooks.add(interceptor);
    }

    public static void registerGlobalListener(ToolExecutionListener listener) {
        globalListeners.add(listener);
    }

    public void addInterceptor(ToolHook interceptor) {
        hooks.add(interceptor);
    }

    public void addListener(ToolExecutionListener listener) {
        listeners.add(listener);
    }

    protected ToolBuilder tool() {
        return new ToolBuilder(this);
    }

    protected static Driver getDriver(Map<String, Object> args) {
        McpConfig config = McpConfig.getInstance();
        String session = args.getOrDefault("session", config.getDefaultSession()).toString();
        ArgumentValidator.validateSession(session);
        Driver driver = DriverManager.getDriver(session);
        if (driver == null) {
            throw new IllegalStateException("Browser not initialized for session: " + session);
        }
        return driver;
    }

    public static ChromeCustom getWebDriver(Map<String, Object> args) {
        Driver driver = getDriver(args);
        if (!(driver instanceof ChromeCustom)) {
            throw new IllegalStateException("Expected ChromeCustom but got " + driver.getClass().getSimpleName());
        }
        return (ChromeCustom) driver;
    }

    public static MobileCustom getMobileDriver(Map<String, Object> args) {
        Driver driver = getDriver(args);
        if (!(driver instanceof MobileCustom)) {
            throw new IllegalStateException("Expected MobileCustom but got " + driver.getClass().getSimpleName());
        }
        return (MobileCustom) driver;
    }

    public McpSchema.CallToolResult execute(
            String toolName,
            Map<String, Object> args,
            Function<Map<String, Object>, String> action,
            boolean record
    ) {
        long startTime = System.currentTimeMillis();

        hooks.forEach(i -> i.before(toolName, args));

        try {
            String result = action.apply(args);
            long duration = System.currentTimeMillis() - startTime;

            hooks.forEach(i -> i.after(toolName, result, duration));

            if (record) {
                listeners.forEach(l -> l.onToolExecuted(toolName, args, result));
            }

            return success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            hooks.forEach(i -> i.onError(toolName, e, duration));
            return error(e);
        }
    }


    protected McpSchema.CallToolResult success(String msg) {
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(msg)), false);
    }

    protected McpSchema.CallToolResult error(Exception e) {
        StringBuilder sb = new StringBuilder("Error: " + e.getMessage());
        if (e.getStackTrace().length > 0) {
            sb.append("\n\nStack trace:\n");
            for (int i = 0; i < Math.min(5, e.getStackTrace().length); i++) {
                sb.append("  at ").append(e.getStackTrace()[i]).append("\n");
            }
        }
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(sb.toString())), true);
    }

    protected <T extends AbstractDriverCommand> Function<Map<String, Object>, T> webCommand(Function<ChromeCustom, T> factory) {
        return args -> factory.apply(getWebDriver(args));
    }

    protected <T extends AbstractDriverCommand> Function<Map<String, Object>, T> mobileCommand(Function<MobileCustom, T> factory) {
        return args -> factory.apply(getMobileDriver(args));
    }



}

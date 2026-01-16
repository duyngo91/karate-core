package core.mcp.tools.registry;

import core.mcp.command.ToolCommand;
import core.mcp.tools.BaseToolExecutor;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.Map;
import java.util.function.Function;




public class ToolBuilder {

    private final BaseToolExecutor executor;

    private String name;
    private String description;

    /**
     * Factory tạo ToolCommand từ args (để lấy driver, session, platform...)
     */
    private Function<Map<String, Object>, ToolCommand> commandFactory;

    public ToolBuilder(BaseToolExecutor executor) {
        this.executor = executor;
    }

    public ToolBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ToolBuilder description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Dùng cho Command Pattern (khuyến nghị)
     * Ví dụ:
     *   .command(webCommand(ClearCommand::new))
     */
    public ToolBuilder command(
            Function<Map<String, Object>, ? extends ToolCommand> factory
    ) {
        this.commandFactory = (Function<Map<String, Object>, ToolCommand>) factory;
        return this;
    }

    /**
     * Build MCP Tool
     */
    public McpServerFeatures.SyncToolSpecification build() {

        if (commandFactory == null) {
            throw new IllegalStateException("ToolCommand factory is required");
        }

        return ToolFactory.create(
                name,
                description,
                (executorInstance, args) -> {

                    ToolCommand command = commandFactory.apply(args);

                    // validate arguments (Strategy Pattern)
                    command.validate(args);

                    // delegate execution to BaseToolExecutor
                    return executor.execute(
                            name,
                            args,
                            a -> String.valueOf(command.execute(a)),
                            command.isRecordable()
                    );
                }
        );
    }
}

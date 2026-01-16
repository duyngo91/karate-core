package core.mcp.tools.web;

import core.mcp.command.web.TableGetDataCommand;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class TableTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.TABLE_GET_DATA)
                .description("Get table data")
                .command(webCommand(TableGetDataCommand::new))
                .build()
        );
    }

    @Override
    public String getCategory() {
        return "TableTools";
    }
}

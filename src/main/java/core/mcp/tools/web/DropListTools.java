package core.mcp.tools.web;

import core.mcp.command.web.DropListGetOptionsCommand;
import core.mcp.command.web.DropListSearchSelectCommand;
import core.mcp.command.web.DropListSelectCommand;
import core.mcp.command.web.DropListSelectContainsCommand;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class DropListTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.DROPLIST_SELECT)
                .description("Select droplist")
                .command(webCommand(DropListSelectCommand::new))
                .build(),

            tool().name(ToolNames.DROPLIST_SELECT_CONTAINS)
                .description("Select droplist contains")
                .command(webCommand(DropListSelectContainsCommand::new))
                .build(),

            tool().name(ToolNames.DROPLIST_SEARCH_SELECT)
                .description("Search & select droplist")
                .command(webCommand(DropListSearchSelectCommand::new))
                .build(),

            tool().name(ToolNames.DROPLIST_GET_OPTIONS)
                .description("Get droplist options")
                .command(webCommand(DropListGetOptionsCommand::new))
                .build()
        );
    }

    @Override
    public String getCategory() {
        return "DropListTools";
    }
}

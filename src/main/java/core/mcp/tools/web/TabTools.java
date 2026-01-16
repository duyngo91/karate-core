package core.mcp.tools.web;

import core.mcp.command.web.*;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class TabTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.OPEN_NEW_TAB)
                .description("Open new tab")
                .command(webCommand(OpenNewTabCommand::new))
                .build(),

            tool().name(ToolNames.SWITCH_TAB)
                .description("Switch tab by title")
                .command(webCommand(SwitchTabCommand::new))
                .build(),

            tool().name(ToolNames.SWITCH_TAB_CONTAINS)
                .description("Switch tab contains")
                .command(webCommand(SwitchTabContainsCommand::new))
                .build(),

            tool().name(ToolNames.GET_TABS)
                .description("Get all tabs")
                .command(webCommand(GetTabsCommand::new))
                .build(),

            tool().name(ToolNames.CLOSE_TAB)
                .description("Close tab by title")
                .command(webCommand(CloseTabCommand::new))
                .build()
        );
    }

    @Override
    public String getCategory() {
        return "TabTools";
    }
}

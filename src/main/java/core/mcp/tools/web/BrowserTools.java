package core.mcp.tools.web;

import core.mcp.command.web.*;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class BrowserTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.INIT_BROWSER)
                .description("Init Chrome browser")
                .command(arg-> new InitBrowserCommand())
                .build(),

            tool().name(ToolNames.NAVIGATE)
                .description("Navigate to URL")
                .command(webCommand(NavigateCommand::new))
                .build(),

            tool().name(ToolNames.GET_PAGE_TITLE)
                .description("Get title")
                .command(webCommand(GetPageTitleCommand::new))
                .build(),

            tool().name(ToolNames.CLOSE)
                .description("Close browser")
                .command(webCommand(CloseBrowserCommand::new))
                .build(),

            tool().name(ToolNames.EXECUTE_SCRIPT)
                .description("Execute JS script")
                .command(webCommand(ExecuteScriptCommand::new))
                .build()
        );
    }

    @Override
    public String getCategory() {
        return "BrowserTools";
    }
}

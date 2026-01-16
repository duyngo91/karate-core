package core.mcp.tools.web;

import core.mcp.command.mobile.MouseClickCommand;
import core.mcp.command.web.ClearCommand;
import core.mcp.command.web.ClickCommand;
import core.mcp.command.web.GetTextCommand;
import core.mcp.command.web.InputCommand;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class FormTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.CLICK)
                .description("Click element")
                .command(webCommand(ClickCommand::new))
                .build(),

            tool().name(ToolNames.MOUSE_CLICK)
                .description("Mouse click element")
                .command(webCommand(MouseClickCommand::new))
                .build(),

            tool().name(ToolNames.INPUT)
                .description("Input text")
                .command(webCommand(InputCommand::new))
                .build(),

            tool().name(ToolNames.CLEAR)
                .description("Clear input")
                .command(webCommand(ClearCommand::new))
                .build(),

            tool().name(ToolNames.GET_TEXT)
                .description("Get text")
                .command(webCommand(GetTextCommand::new))
                .build()
        );
    }

    @Override
    public String getCategory() {
        return "FormTools";
    }
}

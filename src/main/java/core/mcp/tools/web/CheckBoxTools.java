package core.mcp.tools.web;

import core.mcp.command.web.CheckBoxIsCheckedCommand;
import core.mcp.command.web.CheckBoxSetCommand;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class CheckBoxTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(

                tool().name(ToolNames.CHECKBOX_IS_CHECKED)
                        .description("Check checkbox status")
                        .command(webCommand(CheckBoxIsCheckedCommand::new))
                        .build(),

                tool().name(ToolNames.CHECKBOX_SET)
                        .description("Set checkbox")
                        .command(webCommand(CheckBoxSetCommand::new))
                        .build()
        );
    }

    @Override
    public String getCategory() {
        return "CheckBoxTools";
    }
}

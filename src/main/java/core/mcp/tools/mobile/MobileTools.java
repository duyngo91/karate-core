package core.mcp.tools.mobile;

import core.mcp.command.mobile.ConnectAndroidCommand;
import core.mcp.command.mobile.MobileClickCommand;
import core.mcp.command.mobile.MobileCloseCommand;
import core.mcp.command.mobile.MobileGetAllElementsCommand;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class MobileTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.CONNECT_ANDROID)
                .description("Connect Android device")
                .command(arg-> new ConnectAndroidCommand())
                .build(),

            tool().name(ToolNames.MOBILE_CLICK)
                .description("Click mobile element")
                .command(mobileCommand(MobileClickCommand::new))
                .build(),

            tool().name(ToolNames.MOBILE_CLOSE)
                .description("Close mobile")
                .command(mobileCommand(MobileCloseCommand::new))
                .build(),

            tool().name(ToolNames.MOBILE_GET_ALL_ELEMENTS_ON_SCREEN)
                    .description("mobile get all elements on screen")
                    .command(mobileCommand(MobileGetAllElementsCommand::new))
                    .build()
        );
    }

    @Override
    public String getCategory() {
        return "MobileTools";
    }
}

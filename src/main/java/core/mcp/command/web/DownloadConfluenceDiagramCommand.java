package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class DownloadConfluenceDiagramCommand extends AbstractDriverCommand {
    public DownloadConfluenceDiagramCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonNull(args, ToolNames.PAGE_ID, ToolNames.DIAGRAM_NAME, ToolNames.BASE_URL, ToolNames.SAVE_PATH));
    }
    @Override
    public Object execute(Map<String, Object> args) {
        String pageId = args.get(ToolNames.PAGE_ID).toString();
        String diagramName = args.get(ToolNames.DIAGRAM_NAME).toString();
        String baseUrl = args.get(ToolNames.BASE_URL).toString();
        String savePath = args.get(ToolNames.SAVE_PATH).toString();
        return getChrome().downloadConfluenceDiagram(pageId, diagramName, baseUrl, savePath);
    }

}

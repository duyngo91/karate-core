package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class DownloadFileFromUrlCommand extends AbstractDriverCommand {
    public DownloadFileFromUrlCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonNull(args, ToolNames.URL, ToolNames.FILE_NAME));
    }
    @Override
    public Object execute(Map<String, Object> args) {
        String url = args.get(ToolNames.URL).toString();
        String fileName = args.get(ToolNames.FILE_NAME).toString();
        return getChrome().downloadFileFromUrl(url, fileName);
    }

}

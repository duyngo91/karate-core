package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class UploadFileByDragCommand extends AbstractDriverCommand {
    public UploadFileByDragCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonNull(args, ToolNames.LOCATOR, ToolNames.FILE_PATH));
    }


    @Override
    public Object execute(Map<String, Object> args) {

        String locator = args.get(ToolNames.LOCATOR).toString();
        String filePath = args.get(ToolNames.FILE_PATH).toString();
        getChrome().uploadFileByDragEvent(locator, filePath);
        return "File uploaded: " + filePath;
    }

}

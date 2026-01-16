package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.UrlValidationStrategy;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class OpenNewTabCommand extends AbstractDriverCommand {


    public OpenNewTabCommand(ChromeCustom driver) {
        super(driver, new UrlValidationStrategy());
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String url = args.get(ToolNames.URL).toString();
        getChrome().tab().openNewTab(url);
        return "New tab opened";
    }


}

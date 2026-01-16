package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.UrlValidationStrategy;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class NavigateCommand extends AbstractDriverCommand {

    public NavigateCommand(ChromeCustom driver) {
        super(driver, new UrlValidationStrategy());
    }


    @Override
    public Object execute(Map<String, Object> args) {
        String url = args.get(ToolNames.URL).toString();
        getDriver().setUrl(url);
        return "Navigated to " + url;
    }
}

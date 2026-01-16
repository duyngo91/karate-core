package core.mcp.command.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.mcp.command.AbstractDriverCommand;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class GetTabsCommand extends AbstractDriverCommand {
    public GetTabsCommand(ChromeCustom driver) {
        super(driver, null);
    }
    @Override
    public Object execute(Map<String, Object> args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(getChrome().tab().getTabs());
    }

    


}

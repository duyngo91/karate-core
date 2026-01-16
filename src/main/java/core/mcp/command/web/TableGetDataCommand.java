package core.mcp.command.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;
import core.platform.web.element.table.Table;

import java.util.Map;

public class TableGetDataCommand extends AbstractDriverCommand {
    public TableGetDataCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonEmpty(args, ToolNames.HEADER_LOCATOR));
    }

    @Override
    public Object execute(Map<String, Object> args) {
        Table table = (Table) getChrome().table(args.get(ToolNames.HEADER_LOCATOR).toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(table.getData());
    }

    

}

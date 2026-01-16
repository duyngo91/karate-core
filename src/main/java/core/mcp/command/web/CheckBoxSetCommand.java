package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class CheckBoxSetCommand extends AbstractDriverCommand {
    public CheckBoxSetCommand(ChromeCustom driver) {
        super(driver, args -> {
            ArgumentValidator.requireNonNull(
                    args,
                    ToolNames.LOCATOR,
                    ToolNames.CHECKED
            );
        });
    }

    @Override
    public Object execute(Map<String, Object> args) {
        getChrome().setCheckBox(
                args.get(ToolNames.LOCATOR).toString(),
                Boolean.parseBoolean(args.get(ToolNames.CHECKED).toString())
        );
        return "Checkbox set";
    }

    

}

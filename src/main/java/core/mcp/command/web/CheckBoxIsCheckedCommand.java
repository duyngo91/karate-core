package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class CheckBoxIsCheckedCommand extends AbstractDriverCommand {
    public CheckBoxIsCheckedCommand(ChromeCustom driver) {
        super(driver, args -> {
            ArgumentValidator.requireNonEmpty(args, ToolNames.LOCATOR);
            ArgumentValidator.validateLocator(args.get(ToolNames.LOCATOR).toString());
        });
    }

    @Override
    public Object execute(Map<String, Object> args) {
        return getChrome()
                .checked(args.get(ToolNames.LOCATOR).toString())
                .toString();
    }

    

}

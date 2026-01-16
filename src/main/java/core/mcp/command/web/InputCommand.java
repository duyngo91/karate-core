package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.strategy.ValidationStrategy;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class InputCommand extends AbstractDriverCommand {

    public InputCommand(ChromeCustom driver) {
        super(driver, new InputValidationStrategy());
    }


    @Override
    public Object execute(Map<String, Object> args) {
        String locator = args.get(ToolNames.LOCATOR).toString();
        String text = args.get("text").toString();
        getDriver().input(locator, text);
        return "Input text into " + locator;
    }


    private static class InputValidationStrategy implements ValidationStrategy {
        @Override
        public void validate(Map<String, Object> args) {
            ArgumentValidator.requireNonNull(args, ToolNames.LOCATOR, "text");
            ArgumentValidator.requireNonEmpty(args, ToolNames.LOCATOR);
            ArgumentValidator.validateLocator(args.get(ToolNames.LOCATOR).toString());
        }
    }
}

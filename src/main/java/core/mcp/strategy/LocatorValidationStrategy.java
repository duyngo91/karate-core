package core.mcp.strategy;

import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;

import java.util.Map;

public class LocatorValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(Map<String, Object> args) {
        ArgumentValidator.requireNonEmpty(args, ToolNames.LOCATOR);
        ArgumentValidator.validateLocator(args.get(ToolNames.LOCATOR).toString());
    }
}

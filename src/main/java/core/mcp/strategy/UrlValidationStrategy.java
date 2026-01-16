package core.mcp.strategy;

import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;

import java.util.Map;

public class UrlValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(Map<String, Object> args) {
        ArgumentValidator.requireNonEmpty(args, ToolNames.URL);
        ArgumentValidator.validateUrl(args.get(ToolNames.URL).toString());
    }
}

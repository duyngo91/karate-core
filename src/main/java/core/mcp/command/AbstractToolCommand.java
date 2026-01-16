package core.mcp.command;

import core.mcp.strategy.ValidationStrategy;

import java.util.Map;

public abstract class AbstractToolCommand implements ToolCommand {
    protected String name;
    protected ValidationStrategy validationStrategy;
    protected boolean recordable;

    protected AbstractToolCommand() {
        this.name = null;
        this.validationStrategy = null;
        this.recordable = true;
    }

    protected AbstractToolCommand(ValidationStrategy validationStrategy) {
        this.name = null;
        this.validationStrategy = validationStrategy;
        this.recordable = true;
    }

    protected AbstractToolCommand(String name, ValidationStrategy validationStrategy, boolean recordable) {
        this.name = name;
        this.validationStrategy = validationStrategy;
        this.recordable = recordable;
    }

    @Override
    public void validate(Map<String, Object> args) {
        if (validationStrategy != null) {
            validationStrategy.validate(args);
        }
    }

    @Override
    public boolean isRecordable() {
        return recordable;
    }

}

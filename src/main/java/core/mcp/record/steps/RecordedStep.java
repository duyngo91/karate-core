package core.mcp.record.steps;

import java.util.Map;

public class RecordedStep {
    private final String tool;
    private final Map<String, Object> args;

    public RecordedStep(String tool, Map<String, Object> args) {
        this.tool = tool;
        this.args = args;
    }

    public String tool() { return tool; }
    public Map<String, Object> args() { return args; }
}

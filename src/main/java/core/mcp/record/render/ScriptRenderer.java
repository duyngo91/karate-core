package core.mcp.record.render;

import core.mcp.record.steps.RecordedStep;

import java.util.List;

public interface ScriptRenderer {
    public String render(List<RecordedStep> steps);
}


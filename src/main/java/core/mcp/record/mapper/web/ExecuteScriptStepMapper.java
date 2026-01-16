package core.mcp.record.mapper.web;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class ExecuteScriptStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.EXECUTE_SCRIPT.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.star(
                "def result = script('%s')".formatted(
                        step.args().get("script")
                )
        );
    }
}

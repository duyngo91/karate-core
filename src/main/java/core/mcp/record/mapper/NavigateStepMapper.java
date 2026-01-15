package core.mcp.record.mapper;

import core.mcp.constant.ToolNames;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class NavigateStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.NAVIGATE.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.and(
                "When driver '%s'\n* maximize()".formatted(
                        step.args().get(ToolNames.LOCATOR)
                )
        );
    }
}

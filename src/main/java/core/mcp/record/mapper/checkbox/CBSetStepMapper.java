package core.mcp.record.mapper.checkbox;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class CBSetStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.CHECKBOX_SET.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.and(
                "checkbox('%s').set(%s)".formatted(
                        step.args().get(ToolNames.LOCATOR),
                        step.args().get(ToolNames.CHECKED)
                )
        );
    }
}


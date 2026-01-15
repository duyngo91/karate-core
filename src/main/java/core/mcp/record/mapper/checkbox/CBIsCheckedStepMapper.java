package core.mcp.record.mapper.checkbox;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class CBIsCheckedStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.CHECKBOX_IS_CHECKED.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.and(
                "def isChecked = checkbox('%s').isChecked()".formatted(
                        step.args().get(ToolNames.LOCATOR)
                )
        );
    }
}

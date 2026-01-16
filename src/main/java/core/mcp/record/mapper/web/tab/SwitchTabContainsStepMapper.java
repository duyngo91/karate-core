package core.mcp.record.mapper.web.tab;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class SwitchTabContainsStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.SWITCH_TAB_CONTAINS.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.star(
                "tab().switchToTitleContains('%s')".formatted(
                        step.args().get(ToolNames.TITLE)
                )
        );
    }
}

package core.mcp.record.mapper.tab;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class CloseTabStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.CLOSE_TAB.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.and(
                "tab().closeByTitle('%s')".formatted(
                        step.args().get(ToolNames.TITLE)
                )
        );
    }
}

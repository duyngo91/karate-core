package core.mcp.record.mapper.web.tab;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class OpenNewTabStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.OPEN_NEW_TAB.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.star(
                "tab().openNewTab('%s')".formatted(
                        step.args().get(ToolNames.URL)
                )
        );
    }
}

package core.mcp.record.mapper.web.drop_list;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class DLGetOptionsStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.DROPLIST_GET_OPTIONS.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.star(
                "def options = droplist('%s').getOptions()".formatted(
                        step.args().get(ToolNames.LOCATOR)
                )
        );
    }
}

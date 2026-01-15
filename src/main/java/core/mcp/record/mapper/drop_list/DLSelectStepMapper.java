package core.mcp.record.mapper.drop_list;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class DLSelectStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.DROPLIST_SELECT.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.and(
                "droplist('%s').select('%s')".formatted(
                        step.args().get(ToolNames.LOCATOR),
                        step.args().get(ToolNames.VALUE)
                )
        );
    }
}


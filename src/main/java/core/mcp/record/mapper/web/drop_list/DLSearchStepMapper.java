package core.mcp.record.mapper.web.drop_list;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class DLSearchStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.DROPLIST_SEARCH_SELECT.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.star(
                "droplist('%s').searchSelect('%s', '%s', '%s')".formatted(
                        step.args().get(ToolNames.LOCATOR),
                        step.args().get(ToolNames.SEARCH_FIELD),
                        step.args().get(ToolNames.SEARCH_VALUE),
                        step.args().get(ToolNames.SEARCH_VALUE)
                )
        );
    }
}


package core.mcp.record.mapper.web.tab;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class GetTabsStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.GET_TABS.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.star(
                "def tabs = tab().getTabs()"
        );
    }
}


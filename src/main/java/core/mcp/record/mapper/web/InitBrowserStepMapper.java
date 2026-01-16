package core.mcp.record.mapper.web;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class InitBrowserStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.INIT_BROWSER.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.star(
                "configure driver = driverConfig"
        );
    }
}

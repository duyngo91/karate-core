package core.mcp.record.mapper.element;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class GetTextStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.GET_TEXT.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.and(
                "def text = waitFor('%s').text()".formatted(
                        step.args().get(ToolNames.LOCATOR)
                )
        );
    }
}

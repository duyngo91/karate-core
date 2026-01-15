package core.mcp.record.mapper.table;

import core.mcp.constant.ToolNames;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public class TableGetDataStepMapper implements StepMapper {

    @Override
    public boolean supports(String tool) {
        return ToolNames.TABLE_GET_DATA.equals(tool);
    }

    @Override
    public KarateStep map(RecordedStep step) {
        return KarateStep.and(
                "retry(30, 1000).waitFor('%s')\\n* def tableData = table('%s').getData()".formatted(
                        step.args().get(ToolNames.HEADER_LOCATOR),
                        step.args().get(ToolNames.HEADER_LOCATOR)
                )
        );
    }
}
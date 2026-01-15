package core.mcp.record.mapper;

import core.mcp.record.steps.KarateStep;
import core.mcp.record.steps.RecordedStep;

public interface StepMapper {
    boolean supports(String tool);
    KarateStep map(RecordedStep step);
}

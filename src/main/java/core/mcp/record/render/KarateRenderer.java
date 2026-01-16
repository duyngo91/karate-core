package core.mcp.record.render;

import core.mcp.record.mapper.StepMapper;
import core.mcp.record.steps.RecordedStep;

import java.util.List;

public class KarateRenderer implements ScriptRenderer {

    private final List<StepMapper> mappers;

    public KarateRenderer(List<StepMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public String render(List<RecordedStep> steps) {
        StringBuilder sb = new StringBuilder();

        sb.append("Feature: Recorded Test\n\n");
        sb.append("Scenario: Auto generated\n");

        for (RecordedStep step : steps) {
            sb.append(renderStep(step)).append("\n");
        }

        return sb.toString();
    }

    private String renderStep(RecordedStep step) {
        return mappers.stream()
                .filter(m -> m.supports(step.tool()))
                .findFirst()
                .map(m -> m.map(step).render())
                .orElse("  # Unsupported tool: " + step.tool());
    }
}


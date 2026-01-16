package core.mcp.command.record;

import core.mcp.command.AbstractRecordCommand;
import core.mcp.record.ScriptRecorder;
import core.mcp.record.render.KarateRenderer;

import java.util.Map;


public class GetRecordedScriptCommand extends AbstractRecordCommand {

    private final KarateRenderer renderer;

    public GetRecordedScriptCommand(ScriptRecorder recorder, KarateRenderer renderer) {
        super(recorder);
        this.renderer = renderer;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        return renderer.render(recorder.getSteps());
    }

    @Override
    public void validate(Map<String, Object> args) {
    }

    @Override
    public boolean isRecordable() {
        return false;
    }

}

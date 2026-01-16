package core.mcp.command.record;

import core.mcp.command.AbstractRecordCommand;
import core.mcp.record.ScriptRecorder;

import java.util.Map;


public class StopRecordingCommand extends AbstractRecordCommand {

    public StopRecordingCommand(ScriptRecorder recorder) {
        super(recorder);
    }

    @Override
    public Object execute(Map<String, Object> args) {
        getRecorder().stop();
        return "Recording stopped";
    }

    @Override
    public void validate(Map<String, Object> args) {
        // no validation needed
    }

    @Override
    public boolean isRecordable() {
        return false; // ❗ không record chính nó
    }

}

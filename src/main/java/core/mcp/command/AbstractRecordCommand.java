package core.mcp.command;

import core.mcp.record.ScriptRecorder;

public abstract class AbstractRecordCommand implements ToolCommand {
    protected ScriptRecorder recorder;

    protected AbstractRecordCommand(ScriptRecorder recorder) {
        this.recorder = recorder;
    }

    protected ScriptRecorder getRecorder() {
        return recorder;
    }

}

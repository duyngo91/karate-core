package core.mcp.observer;

import core.mcp.record.ScriptRecorder;

import java.util.Map;

public class RecordingListener implements ToolExecutionListener {
    @Override
    public void onToolExecuted(String tool, Map<String, Object> args, String result) {
        ScriptRecorder recorder = ScriptRecorder.getInstance();
        if (recorder.isRecording()) {
            recorder.record(tool, args);
        }
    }
}

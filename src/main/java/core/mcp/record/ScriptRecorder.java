package core.mcp.record;

import core.mcp.record.steps.RecordedStep;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScriptRecorder {

    private static final ScriptRecorder INSTANCE = new ScriptRecorder();
    private final List<RecordedStep> steps = new CopyOnWriteArrayList<>();
    private volatile boolean recording;

    public static ScriptRecorder getInstance() {
        return INSTANCE;
    }

    public void start() {
        steps.clear();
        recording = true;
    }

    public void stop() {
        recording = false;
    }

    public void record(String tool, Map<String, Object> args) {
        if (!recording) return;
        steps.add(new RecordedStep(tool, args));
    }

    public List<RecordedStep> getSteps() {
        return List.copyOf(steps);
    }

    public boolean isRecording() {
        return recording;
    }
}

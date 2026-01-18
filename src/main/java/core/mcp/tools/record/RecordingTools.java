package core.mcp.tools.record;

import core.mcp.command.record.GetRecordedScriptCommand;
import core.mcp.command.record.StartRecordingCommand;
import core.mcp.command.record.StopRecordingCommand;
import core.mcp.constant.ToolNames;
import core.mcp.record.ScriptRecorder;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.render.KarateRenderer;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class RecordingTools extends BaseToolExecutor implements ToolProvider {
;

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.START_RECORDING)
                .description("Start recording")
                .command(args -> new StartRecordingCommand(ScriptRecorder.getInstance()))
                .build(),

            tool().name(ToolNames.STOP_RECORDING)
                .description("Stop recording")
                .command(args -> new StopRecordingCommand(ScriptRecorder.getInstance()))
                .build(),

            tool().name(ToolNames.GET_RECORDED_SCRIPT)
                .description("Get recorded script")
                .command(args -> new GetRecordedScriptCommand(
                        ScriptRecorder.getInstance(),
                        new KarateRenderer(getMappers())
                ))
                .build()

        );
    }

    @Override
    public String getCategory() {
        return "RecordingTools";
    }

    private List<StepMapper> getMappers() {
        ServiceLoader<StepMapper> loader = ServiceLoader.load(StepMapper.class);
        return new ArrayList<>(loader.stream().map(ServiceLoader.Provider::get).toList());
    }

}

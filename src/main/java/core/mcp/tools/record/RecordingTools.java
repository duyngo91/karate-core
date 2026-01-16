package core.mcp.tools.record;

import core.mcp.command.record.GetRecordedScriptCommand;
import core.mcp.command.record.StartRecordingCommand;
import core.mcp.command.record.StopRecordingCommand;
import core.mcp.constant.ToolNames;
import core.mcp.record.ScriptRecorder;
import core.mcp.record.mapper.web.ExecuteScriptStepMapper;
import core.mcp.record.mapper.web.NavigateStepMapper;
import core.mcp.record.mapper.StepMapper;
import core.mcp.record.mapper.web.checkbox.CBIsCheckedStepMapper;
import core.mcp.record.mapper.web.checkbox.CBSetStepMapper;
import core.mcp.record.mapper.web.drop_list.DLGetOptionsStepMapper;
import core.mcp.record.mapper.web.drop_list.DLSearchStepMapper;
import core.mcp.record.mapper.web.drop_list.DLSelectContainsStepMapper;
import core.mcp.record.mapper.web.drop_list.DLSelectStepMapper;
import core.mcp.record.mapper.web.element.*;
import core.mcp.record.mapper.web.tab.GetTabsStepMapper;
import core.mcp.record.mapper.web.tab.OpenNewTabStepMapper;
import core.mcp.record.mapper.web.tab.SwitchTabContainsStepMapper;
import core.mcp.record.mapper.web.tab.SwitchTabStepMapper;
import core.mcp.record.mapper.web.table.TableGetDataStepMapper;
import core.mcp.record.render.KarateRenderer;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.ArrayList;
import java.util.List;

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
        List<StepMapper> mappers = new ArrayList<>();
        mappers.add(new NavigateStepMapper());
        mappers.add(new ClickStepMapper());
        mappers.add(new InputStepMapper());
        mappers.add(new ClearStepMapper());
        mappers.add(new GetTextStepMapper());
        mappers.add(new MouseClickStepMapper());
        mappers.add(new CBSetStepMapper());
        mappers.add(new CBIsCheckedStepMapper());
        mappers.add(new DLSelectStepMapper());
        mappers.add(new DLSelectContainsStepMapper());
        mappers.add(new DLSearchStepMapper());
        mappers.add(new DLGetOptionsStepMapper());
        mappers.add(new OpenNewTabStepMapper());
        mappers.add(new SwitchTabStepMapper());
        mappers.add(new SwitchTabContainsStepMapper());
        mappers.add(new CloseTabStepMapper());
        mappers.add(new GetTabsStepMapper());
        mappers.add(new TableGetDataStepMapper());
        mappers.add(new ExecuteScriptStepMapper());
        return mappers;
    }
}

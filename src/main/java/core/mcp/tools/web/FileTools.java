package core.mcp.tools.web;

import core.mcp.command.web.*;
import core.mcp.constant.ToolNames;
import core.mcp.tools.BaseToolExecutor;
import core.mcp.tools.registry.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class FileTools extends BaseToolExecutor implements ToolProvider {

    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
            tool().name(ToolNames.DOWNLOAD_FILE_FROM_URL)
                .description("Download file from URL")
                .command(webCommand(DownloadFileFromUrlCommand::new))
                .build(),

            tool().name(ToolNames.UPLOAD_FILE_BY_DRAG)
                .description("Upload file by drag event")
                .command(webCommand(UploadFileByDragCommand::new))
                .build(),

            tool().name(ToolNames.DOWNLOAD_CONFLUENCE_DIAGRAM)
                .description("Download Confluence diagram")
                .command(webCommand(DownloadConfluenceDiagramCommand::new))
                .build(),

            tool().name(ToolNames.GET_CONFLUENCE_ATTACHMENTS)
                .description("Get Confluence attachments")
                .command(webCommand(GetConfluenceAttachmentsCommand::new))
                .build(),

            tool().name(ToolNames.GET_CONFLUENCE_PAGE_ID)
                .description("Get Confluence page ID")
                .command(webCommand(GetConfluencePageIdCommand::new))
                .build()
        );
    }

    @Override
    public String getCategory() {
        return "FileTools";
    }
}

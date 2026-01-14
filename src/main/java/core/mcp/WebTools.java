package core.mcp;

import core.mcp.recorder.ScriptRecorder;
import core.mcp.schema.SchemaLoader;
import core.platform.web.ChromeCustom;
import core.platform.web.element.CheckBox;
import core.platform.web.element.droplist.DropList;
import core.platform.web.element.table.Table;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static core.mcp.KarateMCPServer.createTool;

public class WebTools {
    public static ChromeCustom driver;
    public static final ScriptRecorder recorder = ScriptRecorder.getInstance();

    public static List<McpServerFeatures.SyncToolSpecification> getTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();
        addBrowserTools(tools);
        addElementTools(tools);
        addDroplistTools(tools);
        addTabTools(tools);
        addCheckboxTools(tools);
        addTableTools(tools);
        addRecordingTools(tools);
        addNetworkTools(tools);
        return tools;
    }

    // ==================== BROWSER TOOLS ====================
    private static void addBrowserTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("init_browser", "Initialize Chrome browser", (exchange, args) -> {
            try {
                driver = ChromeCustom.start(new HashMap<>());
                if (!recorder.isRecording()) recorder.startRecording();
                recorder.recordStep("init_browser", args);
                return ToolHandler.success("Browser initialized");
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("get_driver_status", "Get current driver status", (exchange, args) -> {
            if (driver == null) return ToolHandler.success("Driver: null");
            try {
                if (!recorder.isRecording()) recorder.startRecording();
                return ToolHandler.success(String.format("Driver: active, URL: %s, Title: %s", 
                    driver.getUrl(), driver.getTitle()));
            } catch (Exception e) {
                return ToolHandler.success("Driver: inactive/dead");
            }
        }));

        tools.add(createTool("navigate", "Navigate to URL", 
            (exchange, args) -> executeWithRecord("navigate", args, a -> {
                driver.setUrl(a.get("url").toString());
                return "Navigated to: " + a.get("url");
            })));

        tools.add(createTool("get_page_title", "Get page title", 
            (exchange, args) -> execute(a -> "Page title: " + driver.getTitle())));

        tools.add(createTool("get_current_url", "Get current URL", 
            (exchange, args) -> execute(a -> "Current URL: " + driver.getUrl())));

        tools.add(createTool("get_page_source", "Get page HTML source", 
            (exchange, args) -> execute(a -> driver.getOptimizedPageSource())));

        tools.add(createTool("close", "Close browser", (exchange, args) -> {
            try {
                if (driver != null) {
                    if (recorder.isRecording()) recorder.stopRecording();
                    driver.quit();
                    driver = null;
                }
                return ToolHandler.success("Browser closed");
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));
    }

    // ==================== ELEMENT TOOLS ====================
    private static void addElementTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("click", "Click element", 
            (exchange, args) -> executeWithRecord("click", args, a -> {
                driver.click(a.get("locator").toString());
                return "Clicked: " + a.get("locator");
            })));

        tools.add(createTool("mouse_click", "Simulate natural mouse click", 
            (exchange, args) -> executeWithRecord("mouse_click", args, a -> {
                driver.mouseClick(a.get("locator").toString());
                return "Mouse clicked: " + a.get("locator");
            })));

        tools.add(createTool("input", "Input text", 
            (exchange, args) -> executeWithRecord("input", args, a -> {
                driver.input(a.get("locator").toString(), a.get("text").toString());
                return "Input completed";
            })));

        tools.add(createTool("input_value", "Input text with event dispatch", 
            (exchange, args) -> executeWithRecord("input_value", args, a -> {
                driver.inputValue(a.get("locator").toString(), a.get("value").toString());
                return "Input value completed: " + a.get("value");
            })));

        tools.add(createTool("clear", "Clear input field", 
            (exchange, args) -> executeWithRecord("clear", args, a -> {
                driver.clear(a.get("locator").toString());
                return "Field cleared: " + a.get("locator");
            })));

        tools.add(createTool("get_text", "Get element text", 
            (exchange, args) -> executeWithRecord("get_text", args, a -> "Text: " + driver.text(a.get("locator").toString()))));

        tools.add(createTool("wait_exist", "Wait for element to exist", 
            (exchange, args) -> executeWithRecord("wait_exist", args, a -> {
                int timeout = a.containsKey("timeout") ? ((Number) a.get("timeout")).intValue() : 10000;
                boolean exists = driver.waitExist(timeout, 500, a.get("locator").toString());
                return "Element exists: " + exists;
            })));

        tools.add(createTool("scroll_into_view", "Scroll element into view", 
            (exchange, args) -> execute(a -> {
                driver.scrollIntoView(a.get("locator").toString());
                return "Scrolled to element";
            })));

        tools.add(createTool("upload_file", "Upload file to input element", 
            (exchange, args) -> execute(a -> {
                driver.uploadFile(a.get("locator").toString(), a.get("file_path").toString());
                return "File uploaded successfully";
            })));

        tools.add(createTool("get_attribute", "Get element attribute", 
            (exchange, args) -> execute(a -> "Attribute value: " + 
                driver.attribute(a.get("locator").toString(), a.get("attribute").toString()))));

        tools.add(createTool("execute_script", "Execute JavaScript", 
            (exchange, args) -> executeWithRecord("execute_script", args, 
                a -> "Script result: " + driver.script(a.get("script").toString()))));

        tools.add(createTool("select_option", "Select option from dropdown", 
            (exchange, args) -> execute(a -> {
                driver.select(a.get("locator").toString(), a.get("option").toString());
                return "Option selected";
            })));

        tools.add(createTool("take_screenshot", "Take screenshot", (exchange, args) -> {
            try {
                if (driver == null) throw new RuntimeException("Browser not initialized");
                String filePath = args.containsKey("file_path") ? args.get("file_path").toString() : "screenshot.jpeg";
                byte[] screenshotBytes = driver.screenshot();
                BufferedImage resized = resizeImage(ImageIO.read(new java.io.ByteArrayInputStream(screenshotBytes)), 800, 600);
                ImageIO.write(resized, "png", new File(filePath));
                return new McpSchema.CallToolResult(List.of(
                    new McpSchema.TextContent(String.format("Screenshot saved: %s (resized to 800x600)", filePath)),
                    new McpSchema.ImageContent(Arrays.asList(McpSchema.Role.USER), 1.0, encodeToBase64(resized), "image/png")
                ), false);
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));
    }

    // ==================== DROPLIST TOOLS ====================
    private static void addDroplistTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("droplist_select", "Select option from droplist by exact text", 
            (exchange, args) -> executeWithRecord("droplist_select", args, a -> {
                getDropList(a).selectText(a.get("value").toString());
                return "Selected: " + a.get("value");
            })));

        tools.add(createTool("droplist_select_contains", "Select option from droplist by partial text", 
            (exchange, args) -> executeWithRecord("droplist_select_contains", args, a -> {
                getDropList(a).selectTextContains(a.get("value").toString());
                return "Selected option containing: " + a.get("value");
            })));

        tools.add(createTool("droplist_search_select", "Search and select option from droplist", 
            (exchange, args) -> executeWithRecord("droplist_search_select", args, a -> {
                String locator = a.get("locator").toString();
                String searchField = a.get("search_field").toString();
                String items = a.containsKey("items") ? a.get("items").toString() : null;
                DropList dl = (DropList) driver.droplist(locator, searchField, items);
                dl.selectTextAfterSearch(a.get("select_value").toString(), a.get("search_value").toString());
                return "Searched and selected: " + a.get("select_value");
            })));

        tools.add(createTool("droplist_get_options", "Get all options from droplist", 
            (exchange, args) -> executeWithRecord("droplist_get_options", args, 
                a -> "Options: " + String.join(", ", getDropList(a).getAllOptionValues()))));

        tools.add(createTool("droplist_get_selected", "Get selected values from droplist", 
            (exchange, args) -> executeWithRecord("droplist_get_selected", args, 
                a -> "Selected: " + String.join(", ", getDropList(a).selected()))));
    }

    // ==================== TAB TOOLS ====================
    private static void addTabTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("switch_tab", "Switch to tab by title", (exchange, args) -> 
            executeWithRecord("switch_tab", args, a -> {
                driver.tab().switchToTitle(a.get("title").toString());
                return "Switched to tab: " + a.get("title");
            })));

        tools.add(createTool("switch_tab_contains", "Switch to tab by title contains", (exchange, args) -> 
            executeWithRecord("switch_tab_contains", args, a -> {
                driver.tab().switchToTitleContains(a.get("title").toString());
                return "Switched to tab containing: " + a.get("title");
            })));

        tools.add(createTool("open_new_tab", "Open new tab with URL", (exchange, args) -> 
            executeWithRecord("open_new_tab", args, a -> {
                driver.tab().openNewTab(a.get("url").toString());
                return "Opened new tab: " + a.get("url");
            })));

        tools.add(createTool("close_tab", "Close tab by title", (exchange, args) -> 
            executeWithRecord("close_tab", args, a -> {
                driver.tab().closeByTitle(a.get("title").toString());
                return "Closed tab: " + a.get("title");
            })));

        tools.add(createTool("get_tabs", "Get all open tabs", (exchange, args) -> 
            executeWithRecord("get_tabs", args, a -> "Tabs: " + driver.tab().getTabs())));
    }

    // ==================== CHECKBOX TOOLS ====================
    private static void addCheckboxTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("checkbox_is_checked", "Check if checkbox is checked", 
            (exchange, args) -> executeWithRecord("checkbox_is_checked", args, 
                a -> "Checked: " + ((CheckBox) driver.checkbox(a.get("locator").toString())).isChecked())));

        tools.add(createTool("checkbox_set", "Set checkbox state", 
            (exchange, args) -> executeWithRecord("checkbox_set", args, a -> {
                boolean checked = (Boolean) a.get("checked");
                ((CheckBox) driver.checkbox(a.get("locator").toString())).set(checked);
                return "Checkbox set to: " + checked;
            })));
    }

    // ==================== TABLE TOOLS ====================
    private static void addTableTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("table_get_headers", "Get table headers", 
            (exchange, args) -> executeWithRecord("table_get_headers", args, 
                a -> "Headers: " + String.join(", ", getTable(a).getHeaders()))));

        tools.add(createTool("table_get_data", "Get all table data", 
            (exchange, args) -> executeWithRecord("table_get_data", args, 
                a -> "Table data: " + getTable(a).getData())));

        tools.add(createTool("table_get_row_data", "Get specific row data", 
            (exchange, args) -> executeWithRecord("table_get_row_data", args, a -> {
                int rowIndex = ((Number) a.get("row_index")).intValue();
                return "Row data: " + getTable(a).getDataOnRow(rowIndex);
            })));

        tools.add(createTool("table_count_rows", "Count table rows", 
            (exchange, args) -> executeWithRecord("table_count_rows", args, 
                a -> "Row count: " + getTable(a).countRows())));

        tools.add(createTool("table_is_empty", "Check if table is empty", 
            (exchange, args) -> executeWithRecord("table_is_empty", args, 
                a -> "Table empty: " + getTable(a).isEmpty())));
    }

    // ==================== RECORDING TOOLS ====================
    private static void addRecordingTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("start_recording", "Start recording Karate script", (exchange, args) -> {
            try {
                recorder.startRecording();
                return ToolHandler.success("Recording started. All subsequent MCP tool calls will be recorded.");
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

        tools.add(createTool("stop_recording", "Stop recording and save Karate script", (exchange, args) -> {
            try {
                // BYPASS: Always try to get script regardless of state
                String script = recorder.stopRecording();
                if (script == null || script.trim().isEmpty()) {
                    script = recorder.getLastRecordedScript(); // Fallback
                }
                return ToolHandler.success("Recording script:\n" + script);
            } catch (Exception e) {
                return ToolHandler.error(e.getMessage());
            }
        }));

    }

    // ==================== AI TOOLS ====================


    // ==================== NETWORK TOOLS ====================
    private static void addNetworkTools(List<McpServerFeatures.SyncToolSpecification> tools) {
        tools.add(createTool("get_response_api", "Get API response data", 
            (exchange, args) -> execute(a -> "Response API: " + driver.getResponseAPI(a.get("url").toString()))));

        tools.add(createTool("get_request_api", "Get API request data", 
            (exchange, args) -> execute(a -> "Request API: " + driver.getRequestAPI(a.get("url").toString()))));

        tools.add(createTool("enable_network_events", "Enable network monitoring", 
            (exchange, args) -> execute(a -> {
                driver.enableNetworkEvents();
                return "Network events enabled";
            })));
    }

    // ==================== HELPER METHODS ====================
    private static McpSchema.CallToolResult execute(Function<Map<String, Object>, String> action) {
        try {
            if (driver == null) throw new RuntimeException("Browser not initialized");
            return ToolHandler.success(action.apply(null));
        } catch (Exception e) {
            return ToolHandler.error(e.getMessage());
        }
    }

    private static McpSchema.CallToolResult executeWithRecord(String toolName, Map<String, Object> args, 
                                                              Function<Map<String, Object>, String> action) {
        try {
            if (driver == null) throw new RuntimeException("Browser not initialized");
            if (recorder.isRecording()) {
                recorder.recordStep(toolName, args);
            }
            String result = action.apply(args);
            return ToolHandler.success(result);
        } catch (Exception e) {
            return ToolHandler.error(e.getMessage());
        }
    }

    private static DropList getDropList(Map<String, Object> args) {
        String locator = args.get("locator").toString();
        String items = args.containsKey("items") ? args.get("items").toString() : null;
        return items != null ? (DropList) driver.droplist(locator, items) : (DropList) driver.droplist(locator);
    }

    private static Table getTable(Map<String, Object> args) {
        String headerLocator = args.get("header_locator").toString();
        return args.containsKey("row_locator") ? 
            new Table(driver, headerLocator, args.get("row_locator").toString()) : 
            new Table(driver, headerLocator);
    }


    private static BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resized;
    }

    private static String encodeToBase64(BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
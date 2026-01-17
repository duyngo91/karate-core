package core.mcp.constant;

import core.mcp.command.web.ExecuteScriptCommand;

public class ToolNames {
    // ===== Parameters =====
    public static final String LOCATOR = "locator";
    public static final String HEADER_LOCATOR = "header_locator";
    public static final String URL = "url";
    public static final String VALUE = "value";
    public static final String SEARCH_FIELD = "search_field";
    public static final String SEARCH_VALUE = "search_value";
    public static final String CHECKED = "checked";
    public static final String TITLE = "title";
    public static final String INFO = "info";
    public static final String SCRIPT = "script";
    public static final String SESSION = "session";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_PATH = "file_path";
    public static final String PAGE_ID = "page_id";
    public static final String DIAGRAM_NAME = "diagram_name";
    public static final String BASE_URL = "base_url";
    public static final String SAVE_PATH = "save_path";

    private ToolNames() {}

    // ===== Browser / Web =====
    public static final String INIT_BROWSER = "init_browser";
    public static final String NAVIGATE = "navigate";
    public static final String CLICK = "click";
    public static final String MOUSE_CLICK = "mouse_click";
    public static final String GET_PAGE_TITLE = "get_page_title";
    public static final String CLOSE = "close";
    public static final String EXECUTE_SCRIPT = "execute_script";
    public static final String GET_SOURCE_PAGE = "get_source_page";
    public static final String GET_OPTIMIZED_SOURCE_PAGE = "get_optimized_source_page";

    // ===== Tab =====
    public static final String OPEN_NEW_TAB = "open_new_tab";
    public static final String CLOSE_TAB = "close_tab";
    public static final String SWITCH_TAB = "switch_tab";
    public static final String SWITCH_TAB_CONTAINS = "switch_tab_contains";
    public static final String GET_TABS = "get_tabs";

    // ===== Input / Form =====
    public static final String INPUT = "input";
    public static final String CLEAR = "clear";
    public static final String GET_TEXT = "get_text";

    // ===== Checkbox =====
    public static final String CHECKBOX_IS_CHECKED = "checkbox_is_checked";
    public static final String CHECKBOX_SET = "checkbox_set";

    // ===== Droplist =====
    public static final String DROPLIST_GET_OPTIONS = "droplist_get_options";
    public static final String DROPLIST_SELECT = "droplist_select";
    public static final String DROPLIST_SELECT_CONTAINS = "droplist_select_contains";
    public static final String DROPLIST_SEARCH_SELECT = "droplist_search_select";

    // ===== Table =====
    public static final String TABLE_GET_DATA = "table_get_data";

    // ===== Mobile =====
    public static final String CONNECT_ANDROID = "connect_android";
    public static final String MOBILE_CLICK = "mobile_click";
    public static final String MOBILE_CLOSE = "mobile_close";
    public static final String MOBILE_GET_ALL_ELEMENTS_ON_SCREEN = "mobile_get_all_elements_on_screen";

    // ===== Recording =====
    public static final String START_RECORDING = "start_recording";
    public static final String STOP_RECORDING = "stop_recording";
    public static final String GET_RECORDED_SCRIPT = "get_recorded_script";
    
    // ===== File & Download =====
    public static final String DOWNLOAD_FILE_FROM_URL = "download_file_from_url";
    public static final String UPLOAD_FILE_BY_DRAG = "upload_file_by_drag";
    
    // ===== Confluence =====
    public static final String DOWNLOAD_CONFLUENCE_DIAGRAM = "download_confluence_diagram";
    public static final String GET_CONFLUENCE_ATTACHMENTS = "get_confluence_attachments";
    public static final String GET_CONFLUENCE_PAGE_ID = "get_confluence_page_id";
}

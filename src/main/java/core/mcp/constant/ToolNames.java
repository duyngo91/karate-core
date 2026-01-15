package core.mcp.constant;

public class ToolNames {
    public static final String LOCATOR = "locator";
    public static final String HEADER_LOCATOR = "header_locator";
    public static final String URL = "url";
    public static final String VALUE = "value";
    public static final String SEARCH_FIELD = "search_field";
    public static final String SEARCH_VALUE = "search_value";
    public static final String CHECKED = "checked";
    public static final String TITLE = "title";

    private ToolNames() {}

    // ===== Browser / Web =====
    public static final String INIT_BROWSER = "init_browser";
    public static final String NAVIGATE = "navigate";
    public static final String CLICK = "click";
    public static final String MOUSE_CLICK = "mouse_click";
    public static final String GET_PAGE_TITLE = "get_page_title";
    public static final String CLOSE = "close";

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

    // ===== Script =====
    public static final String EXECUTE_SCRIPT = "execute_script";

    // ===== Mobile =====
    public static final String CONNECT_ANDROID = "connect_android";
    public static final String MOBILE_CLICK = "mobile_click";
    public static final String MOBILE_CLOSE = "mobile_close";
}

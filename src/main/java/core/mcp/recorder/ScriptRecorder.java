package core.mcp.recorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptRecorder {
    private static ScriptRecorder instance;
    private List<String> recordedSteps;
    private String lastRecordedScript = "";
    private boolean isRecording;
    
    private ScriptRecorder() {
        this.recordedSteps = new ArrayList<>();
        this.isRecording = false;
    }
    
    public static ScriptRecorder getInstance() {
        if (instance == null) {
            instance = new ScriptRecorder();
        }
        return instance;
    }
    
    public void startRecording() {
        this.recordedSteps.clear();
        this.isRecording = true;
        
        recordedSteps.add("Feature: Recorded Test Script");
        recordedSteps.add("");
        recordedSteps.add("Background:");
        recordedSteps.add("");
        recordedSteps.add("Scenario: Recorded automation steps");
    }



    public String stopRecording() {
        this.lastRecordedScript = getRecordedScript();  // Cache script
        this.isRecording = false;
        return this.lastRecordedScript;
    }

    public String getLastRecordedScript() {
        return lastRecordedScript;
    }

    
    public void recordStep(String toolName, Map<String, Object> args) {
        if (!isRecording) return;
        
        String step = convertToKarateStep(toolName, args);
        if (step != null) {
            recordedSteps.add("  " + step);
        }
    }
    
    private String convertToKarateStep(String toolName, Map<String, Object> args) {
        switch (toolName) {
            case "navigate":
                return String.format("When driver '%s'\n* maximize()", args.get("url"));
            case "click":
                return String.format("And waitFor('%s').click()", args.get("locator"));
            case "mouse_click":
                return String.format("And mouseClick('%s')", args.get("locator"));
            case "input":
                return String.format("And waitFor('%s').input('%s')", args.get("locator"), args.get("text"));
            case "clear":
                return String.format("And waitFor('%s').clear()", args.get("locator"));
            case "get_text":
                return String.format("* def text = waitFor('%s').text()", args.get("locator"));
            case "wait_exist":
                if(args.containsKey("retryCount") && args.containsKey("retryInterval")) return String.format("And retry(%s, %s).waitFor('%s')", args.get("retryCount"), args.get("retryInterval"), args.get("locator"));
                if(args.containsKey("retryCount")) return String.format("And retry(%s, 1000).waitFor('%s')", args.get("retryCount"), args.get("locator"));
                return String.format("And waitFor('%s')", args.get("locator"));
            case "execute_script":
                return String.format("* def result = script('%s')", args.get("script").toString().replace("'", "\\'"));
            case "table_get_data":
                return String.format("* retry(30, 1000).waitFor('%s')\n* def tableData = table('%s').getData()", args.get("header_locator"), args.get("header_locator"));
            case "droplist_select":
                return String.format("And droplist('%s').select('%s')", args.get("locator"), args.get("value"));
            case "droplist_select_contains":
                return String.format("And droplist('%s').selectContains('%s')", args.get("locator"), args.get("value"));
            case "droplist_search_select":
                return String.format("And droplist('%s').searchSelect('%s', '%s', '%s')",
                    args.get("locator"), args.get("search_field"), args.get("search_value"), args.get("select_value"));
            case "droplist_get_options":
                return String.format("* def options = droplist('%s').getOptions()", args.get("locator"));
            case "checkbox_is_checked":
                return String.format("* def isChecked = checkbox('%s').isChecked()", args.get("locator"));
            case "checkbox_set":
                return String.format("And checkbox('%s').set(%s)", args.get("locator"), args.get("checked"));
            case "switch_tab":
                return String.format("And tab().switchToTitle('%s')", args.get("title"));
            case "switch_tab_contains":
                return String.format("And tab().switchToTitleContains('%s')", args.get("title"));
            case "open_new_tab":
                return String.format("And tab().openNewTab('%s')", args.get("url"));
            case "close_tab":
                return String.format("And tab().closeByTitle('%s')", args.get("title"));
            case "get_tabs":
                return "* def tabs = tab().getTabs()";
            default:
                return String.format("# %s with args: %s", toolName, args.toString());
        }
    }
    
    public String getRecordedScript() {
        StringBuilder script = new StringBuilder();
        for (String step : recordedSteps) {
            script.append(step).append("\n");
        }
        return script.toString();
    }
    
    public boolean isRecording() {
        return isRecording;
    }
}
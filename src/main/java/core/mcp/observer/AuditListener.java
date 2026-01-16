package core.mcp.observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AuditListener implements ToolExecutionListener {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onToolExecuted(String tool, Map<String, Object> args, String result) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("[AUDIT] " + timestamp + " | Tool: " + tool + " | Args: " + args);
    }
}

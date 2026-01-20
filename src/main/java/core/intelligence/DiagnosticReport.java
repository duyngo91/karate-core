package core.intelligence;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Demo structure for AI Diagnostic Report
 */
@Data
@Builder
public class DiagnosticReport {
    private String failureType; // e.g., "UI_CHANGE", "BACKEND_ERROR", "ENVIRONMENT_ISSUE"
    private double confidence;  // 0.0 to 1.0
    private String analysis;    // Human-readable explanation from AI
    private String suggestion;  // What to do next (e.g., "Update locator to //span[@id='...']")
    private List<String> evidence; // Supporting data like relevant log lines or comparison results
    private long timestamp;


}

package core.healing;

import core.platform.utils.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HealingMonitor {
    private static HealingMonitor instance;
    private final List<HealingEvent> events = new ArrayList<>();

    private HealingMonitor() {
    }

    public static synchronized HealingMonitor getInstance() {
        if (instance == null) {
            instance = new HealingMonitor();
        }
        return instance;
    }

    public void recordEvent(String elementId, String original, String healed, String method, double score,
            boolean success) {
        events.add(new HealingEvent(elementId, original, healed, method, score, success));
        Logger.debug("Healing recorded: %s -> %s (Success: %s)", elementId, healed, success);
    }

    public void generateReport() {
        if (events.isEmpty())
            return;

        java.io.File targetDir = new java.io.File("target");
        if (!targetDir.exists())
            targetDir.mkdirs();

        generateHtmlReport();
    }

    private void generateHtmlReport() {
        String reportPath = "target/healing-report.html";
        long total = events.size();
        long successCount = events.stream().filter(e -> e.success).count();
        double successRate = (double) successCount / total * 100;

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportPath))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='en'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            writer.println("    <title>Self-Healing Insights</title>");
            writer.println(
                    "    <link href='https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap' rel='stylesheet'>");
            writer.println("    <style>");
            writer.println(
                    "        :root { --primary: #6366f1; --success: #22c55e; --danger: #ef4444; --bg: #0f172a; --card-bg: #1e293b; --text: #f8fafc; }");
            writer.println(
                    "        body { font-family: 'Inter', sans-serif; background: var(--bg); color: var(--text); margin: 0; padding: 2rem; }");
            writer.println("        .container { max-width: 1200px; margin: 0 auto; }");
            writer.println(
                    "        header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }");
            writer.println(
                    "        h1 { margin: 0; font-size: 1.8rem; background: linear-gradient(to right, #818cf8, #c084fc); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }");
            writer.println(
                    "        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 2rem; }");
            writer.println(
                    "        .stat-card { background: var(--card-bg); padding: 1.5rem; border-radius: 1rem; border: 1px solid #334155; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }");
            writer.println("        .stat-label { color: #94a3b8; font-size: 0.875rem; margin-bottom: 0.5rem; }");
            writer.println("        .stat-value { font-size: 2rem; font-weight: 700; }");
            writer.println(
                    "        .table-container { background: var(--card-bg); border-radius: 1rem; border: 1px solid #334155; overflow: hidden; }");
            writer.println("        table { width: 100%; border-collapse: collapse; text-align: left; }");
            writer.println("        th { background: #334155; padding: 1rem; font-size: 0.875rem; color: #cbd5e1; }");
            writer.println("        td { padding: 1rem; border-bottom: 1px solid #334155; font-size: 0.875rem; }");
            writer.println("        tr:hover { background: #33415544; }");
            writer.println(
                    "        .badge { padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }");
            writer.println("        .badge-success { background: #14532d; color: #4ade80; }");
            writer.println("        .badge-danger { background: #7f1d1d; color: #f87171; }");
            writer.println(
                    "        .code { font-family: monospace; background: #0f172a; padding: 0.2rem 0.4rem; border-radius: 0.25rem; color: #e2e8f0; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("    <div class='container'>");
            writer.println("        <header>");
            writer.println("            <h1>🛡️ Self-Healing Insights</h1>");
            writer.println("            <div style='color: #94a3b8'>Run at: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</div>");
            writer.println("        </header>");
            writer.println("        <div class='stats-grid'>");
            writer.println(
                    "            <div class='stat-card'><div class='stat-label'>Success Rate</div><div class='stat-value' style='color: var(--success)'>"
                            + String.format("%.1f%%", successRate) + "</div></div>");
            writer.println(
                    "            <div class='stat-card'><div class='stat-label'>Total Healed</div><div class='stat-value'>"
                            + successCount + "</div></div>");
            writer.println(
                    "            <div class='stat-card'><div class='stat-label'>Total Attempts</div><div class='stat-value'>"
                            + total + "</div></div>");
            writer.println("        </div>");
            writer.println("        <div class='table-container'>");
            writer.println("            <table>");
            writer.println(
                    "                <thead><tr><th>Time</th><th>Element ID</th><th>Status</th><th>Original Locator</th><th>Healed To</th><th>Method</th><th>Score</th></tr></thead>");
            writer.println("                <tbody>");

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            for (HealingEvent event : events) {
                String time = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(event.timestamp),
                        java.time.ZoneId.systemDefault()).format(timeFormatter);
                writer.println("                    <tr>");
                writer.println("                        <td>" + time + "</td>");
                writer.println("                        <td><span class='code'>" + event.elementId + "</span></td>");
                writer.println("                        <td><span class='badge "
                        + (event.success ? "badge-success" : "badge-danger") + "'>"
                        + (event.success ? "FIXED" : "FAILED") + "</span></td>");
                writer.println("                        <td><span class='code'>" + escapeHtml(event.original)
                        + "</span></td>");
                writer.println("                        <td><span class='code'>"
                        + (event.healed == null ? "N/A" : escapeHtml(event.healed)) + "</span></td>");
                writer.println("                        <td>" + event.method + "</td>");
                writer.println(
                        "                        <td><strong>" + String.format("%.2f", event.score) + "</strong></td>");
                writer.println("                    </tr>");
            }

            writer.println("                </tbody>");
            writer.println("            </table>");
            writer.println("        </div>");
            writer.println("    </div>");
            writer.println("</body>");
            writer.println("</html>");
            Logger.info("HTML healing report generated: %s", reportPath);
        } catch (IOException e) {
            Logger.error("Failed to generate HTML report: %s", e.getMessage());
        }
    }

    private String escapeHtml(String input) {
        if (input == null)
            return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static class HealingEvent {
        String elementId;
        String original;
        String healed;
        String method;
        double score;
        boolean success;
        long timestamp;

        public HealingEvent(String elementId, String original, String healed, String method, double score,
                boolean success) {
            this.elementId = elementId;
            this.original = original;
            this.healed = healed;
            this.method = method;
            this.score = score;
            this.success = success;
            this.timestamp = System.currentTimeMillis();
        }
    }
}

package core.healing.infrastructure.monitor;

import core.healing.domain.model.HealingEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HtmlReportViewBuilder {

    public String build(List<HealingEvent> events) {
        StringBuilder html = new StringBuilder();
        long total = events.size();
        long successCount = events.stream().filter(e -> e.success).count();
        double successRate = (double) successCount / total * 100;
        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'>");
        html.append("<head>");
        html.append("    <meta charset='UTF-8'>");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("    <title>Self-Healing Insights</title>");
        html.append(
                "    <link href='https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap' rel='stylesheet'>");
        html.append("    <style>");
        html.append(
                "        :root { --primary: #6366f1; --success: #22c55e; --danger: #ef4444; --bg: #0f172a; --card-bg: #1e293b; --text: #f8fafc; }");
        html.append(
                "        body { font-family: 'Inter', sans-serif; background: var(--bg); color: var(--text); margin: 0; padding: 2rem; }");
        html.append("        .container { max-width: 1200px; margin: 0 auto; }");
        html.append(
                "        header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }");
        html.append(
                "        h1 { margin: 0; font-size: 1.8rem; background: linear-gradient(to right, #818cf8, #c084fc); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }");
        html.append(
                "        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 2rem; }");
        html.append(
                "        .stat-card { background: var(--card-bg); padding: 1.5rem; border-radius: 1rem; border: 1px solid #334155; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }");
        html.append("        .stat-label { color: #94a3b8; font-size: 0.875rem; margin-bottom: 0.5rem; }");
        html.append("        .stat-value { font-size: 2rem; font-weight: 700; }");
        html.append(
                "        .table-container { background: var(--card-bg); border-radius: 1rem; border: 1px solid #334155; overflow: hidden; }");
        html.append("        table { width: 100%; border-collapse: collapse; text-align: left; }");
        html.append("        th { background: #334155; padding: 1rem; font-size: 0.875rem; color: #cbd5e1; }");
        html.append("        td { padding: 1rem; border-bottom: 1px solid #334155; font-size: 0.875rem; }");
        html.append("        tr:hover { background: #33415544; }");
        html.append(
                "        .badge { padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }");
        html.append("        .badge-success { background: #14532d; color: #4ade80; }");
        html.append("        .badge-danger { background: #7f1d1d; color: #f87171; }");
        html.append(
                "        .code { font-family: monospace; background: #0f172a; padding: 0.2rem 0.4rem; border-radius: 0.25rem; color: #e2e8f0; }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class='container'>");
        html.append("        <header>");
        html.append("            <h1>🛡️ Self-Healing Insights</h1>");
        html.append("            <div style='color: #94a3b8'>Run at: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</div>");
        html.append("        </header>");
        html.append("        <div class='stats-grid'>");
        html.append(
                "            <div class='stat-card'><div class='stat-label'>Success Rate</div><div class='stat-value' style='color: var(--success)'>"
                        + String.format("%.1f%%", successRate) + "</div></div>");
        html.append(
                "            <div class='stat-card'><div class='stat-label'>Total Healed</div><div class='stat-value'>"
                        + successCount + "</div></div>");
        html.append(
                "            <div class='stat-card'><div class='stat-label'>Total Attempts</div><div class='stat-value'>"
                        + total + "</div></div>");
        html.append("        </div>");
        html.append("        <div class='table-container'>");
        html.append("            <table>");
        html.append(
                "                <thead><tr><th>Time</th><th>Element ID</th><th>Status</th><th>Original Locator</th><th>Healed To</th><th>Method</th><th>Score</th></tr></thead>");
        html.append("                <tbody>");

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (HealingEvent event : events) {
            String time = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(event.timestamp),
                    java.time.ZoneId.systemDefault()).format(timeFormatter);
            html.append("                    <tr>");
            html.append("                        <td>" + time + "</td>");
            html.append("                        <td><span class='code'>" + event.elementId + "</span></td>");
            html.append("                        <td><span class='badge "
                    + (event.success ? "badge-success" : "badge-danger") + "'>"
                    + (event.success ? "FIXED" : "FAILED") + "</span></td>");
            html.append("                        <td><span class='code'>" + escapeHtml(event.original)
                    + "</span></td>");
            html.append("                        <td><span class='code'>"
                    + (event.healed == null ? "N/A" : escapeHtml(event.healed)) + "</span></td>");
            html.append("                        <td>" + event.method + "</td>");
            html.append(
                    "                        <td><strong>" + String.format("%.2f", event.score) + "</strong></td>");
            html.append("                    </tr>");
        }

        html.append("                </tbody>");
        html.append("            </table>");
        html.append("        </div>");
        html.append("    </div>");
        html.append("</body>");
        html.append("</html>");


        return html.toString();
    }

    private String td(String v) {
        return "<td>" + (v == null ? "" : v) + "</td>";
    }

    private String tdStatus(boolean success) {
        return "<td class='" + (success ? "success" : "fail") + "'>"
                + (success ? "SUCCESS" : "FAILED")
                + "</td>";
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
}

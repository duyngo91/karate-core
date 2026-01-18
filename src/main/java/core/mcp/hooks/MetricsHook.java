package core.mcp.hooks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsHook implements ToolHook {
    private final Map<String, AtomicLong> executionCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> errorCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalDuration = new ConcurrentHashMap<>();

    @Override
    public void after(String toolName, String result, long durationMs) {
        executionCount.computeIfAbsent(toolName, k -> new AtomicLong()).incrementAndGet();
        totalDuration.computeIfAbsent(toolName, k -> new AtomicLong()).addAndGet(durationMs);
    }

    @Override
    public void onError(String toolName, Exception e, long durationMs) {
        errorCount.computeIfAbsent(toolName, k -> new AtomicLong()).incrementAndGet();
    }

    public Map<String, ToolStats> getStats() {
        Map<String, ToolStats> stats = new ConcurrentHashMap<>();
        executionCount.forEach((tool, count) -> {
            long total = totalDuration.getOrDefault(tool, new AtomicLong()).get();
            long errors = errorCount.getOrDefault(tool, new AtomicLong()).get();
            long avg = count.get() > 0 ? total / count.get() : 0;
            stats.put(tool, new ToolStats(count.get(), errors, total, avg));
        });
        return stats;
    }

    public static class ToolStats {
        public final long executionCount;
        public final long errorCount;
        public final long totalDuration;
        public final long avgDuration;

        public ToolStats(long executionCount, long errorCount, long totalDuration, long avgDuration) {
            this.executionCount = executionCount;
            this.errorCount = errorCount;
            this.totalDuration = totalDuration;
            this.avgDuration = avgDuration;
        }

        @Override
        public String toString() {
            return String.format("executions=%d, errors=%d, total=%dms, avg=%dms", 
                executionCount, errorCount, totalDuration, avgDuration);
        }
    }
}

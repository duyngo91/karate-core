package core.mcp.observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsListener implements ToolExecutionListener {
    private final Map<String, AtomicLong> executionCounts = new ConcurrentHashMap<>();

    @Override
    public void onToolExecuted(String tool, Map<String, Object> args, String result) {
        executionCounts.computeIfAbsent(tool, k -> new AtomicLong()).incrementAndGet();
    }

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new ConcurrentHashMap<>();
        executionCounts.forEach((k, v) -> stats.put(k, v.get()));
        return stats;
    }
}

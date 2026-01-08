package core.healing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;

public class LocatorHistory {
    private static final String HISTORY_FILE = "locator-history.json";
    private final Map<String, List<LocatorRecord>> history = new HashMap<>();
    private final Gson gson = new Gson();

    public static class LocatorRecord {
        public String locator;
        public long timestamp;
        public boolean success;
        public int useCount;

        public LocatorRecord(String locator, boolean success) {
            this.locator = locator;
            this.timestamp = System.currentTimeMillis();
            this.success = success;
            this.useCount = 1;
        }
    }

    public void recordSuccess(String elementId, String locator) {
        record(elementId, locator, true);
    }

    public void recordFailure(String elementId, String locator) {
        record(elementId, locator, false);
    }

    private void record(String elementId, String locator, boolean success) {
        history.putIfAbsent(elementId, new ArrayList<>());
        List<LocatorRecord> records = history.get(elementId);

        Optional<LocatorRecord> existing = records.stream()
                .filter(r -> r.locator.equals(locator))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().useCount++;
            existing.get().success = success;
            existing.get().timestamp = System.currentTimeMillis();
        } else {
            records.add(new LocatorRecord(locator, success));
        }
    }

    public List<String> predictBestLocators(String elementId) {
        List<LocatorRecord> records = history.get(elementId);
        if (records == null || records.isEmpty())
            return new ArrayList<>();

        // Auto cleanup old records (>30 days)
        cleanupOldRecords(records);

        records.sort((a, b) -> {
            int scoreA = calculateScore(a);
            int scoreB = calculateScore(b);
            return Integer.compare(scoreB, scoreA);
        });

        List<String> result = new ArrayList<>();
        for (LocatorRecord record : records) {
            if (record.success && !isExpired(record)) {
                result.add(record.locator);
            }
        }
        return result;
    }

    private int calculateScore(LocatorRecord record) {
        int score = 0;
        if (record.success)
            score += 100;
        score += record.useCount * 10;

        long age = System.currentTimeMillis() - record.timestamp;
        long dayInMs = 24 * 60 * 60 * 1000;
        if (age < dayInMs)
            score += 50;
        else if (age < 7 * dayInMs)
            score += 20;

        return score;
    }

    private void cleanupOldRecords(List<LocatorRecord> records) {
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        records.removeIf(record -> record.timestamp < thirtyDaysAgo);
    }

    private boolean isExpired(LocatorRecord record) {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        return record.timestamp < sevenDaysAgo && record.useCount < 3;
    }

    public void save() {
        try (Writer writer = new FileWriter(HISTORY_FILE)) {
            gson.toJson(history, writer);
        } catch (IOException e) {
            // Silent fail
        }
    }

    public void load() {
        File file = new File(HISTORY_FILE);
        if (!file.exists())
            return;

        try (Reader reader = new FileReader(file)) {
            Map<String, List<LocatorRecord>> loaded = gson.fromJson(reader,
                    new TypeToken<Map<String, List<LocatorRecord>>>() {
                    }.getType());
            if (loaded != null)
                history.putAll(loaded);
        } catch (IOException e) {
            // Silent fail
        }
    }
}

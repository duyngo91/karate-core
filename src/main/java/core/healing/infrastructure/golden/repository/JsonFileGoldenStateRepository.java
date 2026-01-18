package core.healing.infrastructure.golden.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import core.healing.domain.model.ElementMetadata;
import core.platform.utils.Logger;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonFileGoldenStateRepository implements GoldenStateRepository {

    private final Path filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, ElementMetadata> storage = new HashMap<>();

    public JsonFileGoldenStateRepository(String filePath) {
        this.filePath = Path.of(filePath);
        loadFromFile();
    }

    @Override
    public synchronized void save(String elementId, ElementMetadata metadata) {
        storage.put(elementId, metadata);
        persist();
    }

    @Override
    public ElementMetadata find(String elementId) {
        return storage.get(elementId);
    }


    @Override
    public synchronized Map<String, ElementMetadata> findAll() {
        return storage;
    }

    @Override
    public synchronized void clear() {
        storage.clear();
        persist();
    }

    // ---------- Internal ----------

    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            Logger.info("GoldenState JSON not found, starting fresh.");
            return;
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            Type type = new TypeToken<Map<String, ElementMetadata>>() {}.getType();
            Map<String, ElementMetadata> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                storage.putAll(loaded);
            }
        } catch (Exception e) {
            Logger.error("Failed to load GoldenState JSON: %s", e.getMessage());
        }
    }

    private void persist() {
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(storage, writer);
        } catch (Exception e) {
            Logger.error("Failed to persist GoldenState JSON: %s", e.getMessage());
        }
    }
}


package core.healing.infrastructure.embedding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.healing.application.port.VectorStoreAdapter;
import core.healing.domain.model.ElementMetadata;
import core.platform.utils.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to migrate data from element-metadata.json to a Vector Store.
 */
public class ElementMigrationService {

    private final VectorStoreAdapter vectorStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ElementMigrationService(VectorStoreAdapter vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void migrate(String jsonFilePath) {
        Logger.info("Starting migration from {} to Vector Store", jsonFilePath);
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            Logger.warn("Migration file not found: {}", jsonFilePath);
            return;
        }

        try {
            Map<String, ElementMetadata> data = objectMapper.readValue(file, new TypeReference<Map<String, ElementMetadata>>() {});
            
            data.forEach((elementId, metadata) -> {
                if (metadata.getVector() != null) {
                    Map<String, String> metadataMap = new HashMap<>();
                    metadataMap.put("tagName", metadata.getTagName());
                    metadataMap.put("locator", metadata.getLocator());
                    metadataMap.put("text", metadata.getText());
                    
                    vectorStore.upsertElement(elementId, metadata.getVector(), metadataMap);
                }
            });
            
            Logger.info("Migration completed successfully. Migrated {} elements.", data.size());
        } catch (IOException e) {
            Logger.error("Failed to migrate data from JSON: {}", e.getMessage(), e);
        }
    }
}

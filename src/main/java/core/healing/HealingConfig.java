package core.healing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.platform.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HealingConfig {
    private static HealingConfig instance;
    private static final String CONFIG_PATH = "healing-config.json";

    // Constants used by V2 strategies
    public static final double HEALING_THRESHOLD = 0.5;
    public static final boolean ENABLE_DEBUG_LOGGING = true;
    public static final boolean ENABLE_PERFORMANCE_LOGGING = false;
    public static final int MAX_CANDIDATES = 50;

    @JsonProperty("enabled")
    public boolean enabled = true;

    @JsonProperty("captureGoldenState")
    public boolean captureGoldenState = true;

    @JsonProperty("semanticEnabled")
    public boolean semanticEnabled = true;

    @JsonProperty("semanticMode")
    public String semanticMode = "HYBRID"; // Options: "LEGACY", "HYBRID"

    @JsonProperty("locatorPath")
    public String locatorPath = "src/test/java/web/locators";

    @JsonProperty("strategies")
    public List<String> strategies = new ArrayList<>();

    public static synchronized HealingConfig getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }

    public boolean isEnabled() {
        String prop = System.getProperty("auto.healing");
        return prop != null ? "true".equalsIgnoreCase(prop) : enabled;
    }

    public boolean isCaptureGoldenState() {
        String prop = System.getProperty("healing.capture");
        return prop != null ? "true".equalsIgnoreCase(prop) : captureGoldenState;
    }

    public boolean isSemanticEnabled() {
        String prop = System.getProperty("healing.semantic");
        return prop != null ? "true".equalsIgnoreCase(prop) : semanticEnabled;
    }

    public String getLocatorPath() {
        String prop = System.getProperty("locator.path");
        return prop != null ? prop : locatorPath;
    }

    public List<String> getStrategies() {
        return strategies;
    }

    private static HealingConfig loadConfig() {
        ObjectMapper mapper = new ObjectMapper();
        File configFile = new File(CONFIG_PATH);
        if (configFile.exists()) {
            try {
                return mapper.readValue(configFile, HealingConfig.class);
            } catch (IOException e) {
                Logger.error("Failed to load healing-config.json: %s. Using defaults.", e.getMessage());
            }
        } else {
            Logger.info("healing-config.json not found. Creating default configuration.");
            return createDefaultConfig();
        }
        return createDefaultConfig();
    }

    private static HealingConfig createDefaultConfig() {
        HealingConfig config = new HealingConfig();

        // Default Strategies
        config.strategies.add("ExactAttributeStrategy");
        config.strategies.add("KeyBasedStrategy");
        config.strategies.add("TextBasedStrategy");
        config.strategies.add("CrossAttributeStrategy");
        config.strategies.add("SemanticValueStrategy");
        config.strategies.add("StructuralStrategy"); // Removed duplicate
        config.strategies.add("NeighborStrategy");
        config.strategies.add("LocationHealingStrategy");
        config.strategies.add("RagHealingStrategy");

        // Save default config for user to edit
        saveConfig(config);

        return config;
    }

    private static void saveConfig(HealingConfig config) {
        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_PATH), config);
        } catch (IOException e) {
            Logger.error("Failed to save default healing-config.json: %s", e.getMessage());
        }
    }
}

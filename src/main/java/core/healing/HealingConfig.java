package core.healing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import core.platform.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HealingConfig {
    private static HealingConfig instance;
    private static final String CONFIG_PATH = "healing-config.yml";
    private static final String LEGACY_CONFIG_PATH = "healing-config.json";

    // Constants used by V2 strategies
    public static final double CONFIDENCE_STRONG = 0.75;
    public static final double CONFIDENCE_OK     = 0.45;
    public static final double CONFIDENCE_WEAK   = 0.35;

    public static final double HEALING_THRESHOLD = 0.5;
    public static final double VISUAL_THRESHOLD = 0.8;
    public static final boolean ENABLE_DEBUG_LOGGING = true;
    public static final boolean ENABLE_PERFORMANCE_LOGGING = false;
    public static final int MAX_CANDIDATES = 100;

    @JsonProperty("enabled")
    public boolean enabled = true;

    @JsonProperty("captureGoldenState")
    public boolean captureGoldenState = true;

    @JsonProperty("ragEnabled")
    public boolean ragEnabled = true;

    @JsonProperty("cacheEnabled")
    public boolean cacheEnabled = true;

    @JsonProperty("semanticEnabled")
    public boolean semanticEnabled = true;

    @JsonProperty("semanticMode")
    public String semanticMode = "HYBRID"; // Options: "LEGACY", "HYBRID"

    @JsonProperty("healingMode")
    public String healingMode = "SAFE"; // Options: "SAFE", "RECKLESS"

    @JsonProperty("executionMode")
    public String executionMode = "SINGLE"; // Options: "SINGLE", "PARALLEL"

    @JsonProperty("maxHealingThreads")
    public int maxHealingThreads = 4;

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

    public boolean isRagEnabled() {
        String prop = System.getProperty("healing.rag");
        return prop != null ? "true".equalsIgnoreCase(prop) : ragEnabled;
    }

    public boolean isCacheEnabled() {
        String prop = System.getProperty("healing.cache");
        return prop != null ? "true".equalsIgnoreCase(prop) : cacheEnabled;
    }

    public boolean isSemanticEnabled() {
        String prop = System.getProperty("healing.semantic");
        return prop != null ? "true".equalsIgnoreCase(prop) : semanticEnabled;
    }

    public String getHealingMode() {
        String prop = System.getProperty("healing.mode");
        return prop != null ? prop.toUpperCase() : (healingMode != null ? healingMode.toUpperCase() : "SAFE");
    }

    public String getExecutionMode() {
        String prop = System.getProperty("healing.execution.mode");
        return prop != null ? prop.toUpperCase() : (executionMode != null ? executionMode.toUpperCase() : "SINGLE");
    }

    public int getMaxHealingThreads() {
        String prop = System.getProperty("healing.max.threads");
        return prop != null ? Integer.parseInt(prop) : maxHealingThreads;
    }

    public String getLocatorPath() {
        String prop = System.getProperty("locator.path");
        return prop != null ? prop : locatorPath;
    }

    public List<String> getStrategies() {
        return strategies;
    }

    private static HealingConfig loadConfig() {
        ObjectMapper mapper = new YAMLMapper();
        File configFile = new File(CONFIG_PATH);
        File legacyFile = new File(LEGACY_CONFIG_PATH);

        if (configFile.exists()) {
            try {
                return mapper.readValue(configFile, HealingConfig.class);
            } catch (IOException e) {
                Logger.error("Failed to load healing-config.yml: %s. Using defaults.", e.getMessage());
            }
        } else if (legacyFile.exists()) {
            Logger.info("Found legacy healing-config.json. Converting to YAML...");
            try {
                ObjectMapper jsonMapper = new ObjectMapper();
                HealingConfig config = jsonMapper.readValue(legacyFile, HealingConfig.class);
                saveConfig(config); // Save as YAML
                // Should we delete JSON? Done later in workflow
                return config;
            } catch (IOException e) {
                Logger.error("Failed to convert legacy healing-config.json: %s", e.getMessage());
            }
        } else {
            Logger.info("healing-config.yml not found. Creating default configuration.");
            return createDefaultConfig();
        }
        return createDefaultConfig();
    }

    private static HealingConfig createDefaultConfig() {
        HealingConfig config = new HealingConfig();

        // Default Strategies
        config.strategies.add("ExactAttributeStrategy");
        config.strategies.add("KeyBasedStrategy");
        config.strategies.add("StructuralStrategy");
        config.strategies.add("CrossAttributeStrategy");
        config.strategies.add("TextBasedStrategy");
        config.strategies.add("NeighborStrategy");
        config.strategies.add("SemanticValueStrategy");
        config.strategies.add("RagHealingStrategy");
        config.strategies.add("VisualHealingStrategy");

        // Save default config for user to edit
        saveConfig(config);

        return config;
    }

    private static void saveConfig(HealingConfig config) {
        try {
            new YAMLMapper().writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_PATH), config);
            Logger.info("Saved healing configuration to: %s", CONFIG_PATH);
        } catch (IOException e) {
            Logger.error("Failed to save healing-config.yml: %s", e.getMessage());
        }
    }
}

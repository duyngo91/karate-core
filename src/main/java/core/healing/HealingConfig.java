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

    @JsonProperty("attributes")
    public List<AttributeConfig> attributes = new ArrayList<>();

    @JsonProperty("semanticEnabled")
    public boolean semanticEnabled = true;

    @JsonProperty("minSimilarityThreshold")
    public double minSimilarityThreshold = 0.4;

    @JsonProperty("locatorPath")
    public String locatorPath = "src/test/java/web/locators";

    @JsonProperty("visualAi")
    public VisualAiConfig visualAi = new VisualAiConfig();

    public static class VisualAiConfig {
        public boolean enabled = true;
        public String modelPath = "models/ui-detector.onnx";
        public double confidenceThreshold = 0.5;
    }

    public static class AttributeConfig {
        public String name;
        public double weight;
        public int priority;
        public List<String> dynamicPatterns = new ArrayList<>();
    }

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

    public boolean isSemanticEnabled() {
        String prop = System.getProperty("healing.semantic");
        return prop != null ? "true".equalsIgnoreCase(prop) : semanticEnabled;
    }

    public boolean isVisualEnabled() {
        String prop = System.getProperty("healing.visual");
        return prop != null ? "true".equalsIgnoreCase(prop) : visualAi.enabled;
    }

    public String getLocatorPath() {
        String prop = System.getProperty("locator.path");
        return prop != null ? prop : locatorPath;
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

        config.attributes.add(createAttr("id", 0.4, 1, "^\\d+$", ".*[_-]\\d{8,}$", "^ember", "^v-", "^j_idt"));
        config.attributes.add(createAttr("data-testid", 0.35, 2));
        config.attributes.add(createAttr("translate", 0.3, 3));
        config.attributes.add(createAttr("name", 0.25, 4));
        config.attributes.add(createAttr("aria-label", 0.2, 5));
        config.attributes.add(createAttr("placeholder", 0.1, 6));

        // Save default config for user to edit
        saveConfig(config);

        return config;
    }

    private static AttributeConfig createAttr(String name, double weight, int priority, String... patterns) {
        AttributeConfig attr = new AttributeConfig();
        attr.name = name;
        attr.weight = weight;
        attr.priority = priority;
        if (patterns != null) {
            for (String p : patterns)
                attr.dynamicPatterns.add(p);
        }
        return attr;
    }

    private static void saveConfig(HealingConfig config) {
        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_PATH), config);
        } catch (IOException e) {
            Logger.error("Failed to save default healing-config.json: %s", e.getMessage());
        }
    }
}

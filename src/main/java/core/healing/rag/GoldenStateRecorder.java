package core.healing.rag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import core.healing.HealingConfig;
import core.healing.IHealingDriver;
import core.platform.utils.Logger;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Capture and store "Golden State" metadata for elements when they are
 * successfully found.
 */
public class GoldenStateRecorder {
    private static GoldenStateRecorder instance;
    private static final String METADATA_FILE = "element-metadata.json";

    private final Map<String, ElementMetadata> metadataMap = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private GoldenStateRecorder() {
        load();
    }

    public static synchronized GoldenStateRecorder getInstance() {
        if (instance == null) {
            instance = new GoldenStateRecorder();
        }
        return instance;
    }

    public void captureAndSave(IHealingDriver driver, String elementId, String locator) {
        if (elementId == null || locator == null)
            return;

        try {
            System.err.println("DEBUG: Attempting to capture Golden State for " + elementId);
            String js = "(function() { " +
                    "var el = null;" +
                    "if (arguments[0].startsWith('/')) {" +
                    "  el = document.evaluate(arguments[0], document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
                    +
                    "} else {" +
                    "  el = document.querySelector(arguments[0]);" +
                    "}" +
                    "if (!el) return null;" +
                    "var attrs = {};" +
                    "for (var i = 0; i < el.attributes.length; i++) {" +
                    "  attrs[el.attributes[i].name] = el.attributes[i].value;" +
                    "}" +
                    "var rect = el.getBoundingClientRect();" +
                    "function getStructuralPath(el) {" +
                    "  var path = [];" +
                    "  var cur = el;" +
                    "  var limit = 10;" +
                    "  while (cur && limit-- > 0) {" +
                    "    path.push(cur.tagName.toLowerCase());" +
                    "    cur = cur.parentElement;" +
                    "  }" +
                    "  return path.join(' > ');" +
                    "};" +
                    "return {" +
                    "  tagName: el.tagName.toLowerCase()," +
                    "  text: el.textContent ? el.textContent.substring(0, 200).trim() : ''," +
                    "  attributes: attrs," +
                    "  neighborText: prev ? (prev.textContent ? prev.textContent.trim() : '') : ''," +
                    "  structuralPath: getStructuralPath(el)," +
                    "  x: rect.left, y: rect.top, w: rect.width, h: rect.height" +
                    "};" +
                    "})();";

            // Execute JS. Note: The driver must support returning Map/Object from JS.
            // In Karate/Selenium, returning a Map/JSON object usually works.
            // We pass 'locator' as an argument, but 'script' method in IHealingDriver just
            // takes a string.
            // We need to inject the locator into the string or use a formatted string.
            String finalJs = js.replace("arguments[0]", "'" + locator.replace("'", "\\'") + "'");

            Object result = driver.script(finalJs);
            if (result != null) {
                System.err.println("DEBUG: Script result type: " + result.getClass().getName());
            } else {
                System.err.println("DEBUG: Script result is NULL");
            }

            if (result instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) result;

                ElementMetadata meta = new ElementMetadata();
                meta.setElementId(elementId);
                meta.setLocator(locator);
                meta.setTagName((String) data.getOrDefault("tagName", ""));
                meta.setText((String) data.getOrDefault("text", ""));
                meta.setNeighborText((String) data.getOrDefault("neighborText", ""));
                meta.setStructuralPath((String) data.getOrDefault("structuralPath", ""));

                if (data.containsKey("attributes") && data.get("attributes") instanceof Map) {
                    Map<String, String> attrs = new HashMap<>();
                    Map<String, Object> rawAttrs = (Map<String, Object>) data.get("attributes");
                    for (Map.Entry<String, Object> entry : rawAttrs.entrySet()) {
                        attrs.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    meta.setAttributes(attrs);
                }

                if (data.get("x") instanceof Number)
                    meta.setX(((Number) data.get("x")).intValue());
                if (data.get("y") instanceof Number)
                    meta.setY(((Number) data.get("y")).intValue());
                if (data.get("w") instanceof Number)
                    meta.setWidth(((Number) data.get("w")).intValue());
                if (data.get("h") instanceof Number)
                    meta.setHeight(((Number) data.get("h")).intValue());

                // Generate Embedding

                String context = ElementMetadata.buildEmbed(meta.getTagName(), meta.getText(),
                        meta.getAttributes(), meta.getNeighborText());

                try {
                    float[] vector = EmbeddingService.getInstance().embed(context);
                    meta.setVector(vector);
                } catch (Throwable t) {
                    Logger.warn("Embedding generation failed, saving without vector: %s", t.getMessage());
                }

                // Capture screenshot for Visual Healing (only if strategy is enabled)
                try {
                    // Check if VisualHealingStrategy is in the config
                    java.util.List<String> strategies = HealingConfig.getInstance().getStrategies();
                    boolean visualEnabled = strategies != null && strategies.contains("VisualHealingStrategy");

                    if (visualEnabled) {
                        core.healing.visual.VisualService visualService = core.healing.visual.VisualService
                                .getInstance();
                        if (visualService.isAvailable()) {
                            java.awt.image.BufferedImage screenshot = visualService.captureScreenshot(driver, locator);
                            if (screenshot != null) {
                                visualService.saveScreenshot(screenshot, elementId);
                                Logger.debug("Screenshot captured for: %s", elementId);
                            }
                        }
                    }
                } catch (Throwable t) {
                    Logger.debug("Screenshot capture failed (non-critical): %s", t.getMessage());
                }

                metadataMap.put(elementId, meta);
                save();
                Logger.debug("Captured Golden State for: %s", elementId);
            }
        } catch (Exception e) {
            Logger.warn("Failed to capture golden state for %s: %s", elementId, e.getMessage());
        }
    }

    public ElementMetadata getMetadata(String elementId) {
        return metadataMap.get(elementId);
    }

    public Map<String, ElementMetadata> getAllMetadata() {
        return metadataMap;
    }

    private void save() {
        try (Writer writer = new FileWriter(METADATA_FILE)) {
            gson.toJson(metadataMap, writer);
        } catch (IOException e) {
            Logger.error("Failed to save element metadata: %s", e.getMessage());
        }
    }

    private void load() {
        File file = new File(METADATA_FILE);
        if (!file.exists())
            return;

        try (Reader reader = new FileReader(file)) {
            Map<String, ElementMetadata> loaded = gson.fromJson(reader,
                    new TypeToken<Map<String, ElementMetadata>>() {
                    }.getType());
            if (loaded != null) {
                metadataMap.putAll(loaded);
            }
        } catch (IOException e) {
            Logger.error("Failed to load element metadata: %s", e.getMessage());
        }
    }

    public void clear() {
        try {
            boolean deleted = Files.deleteIfExists(Path.of(METADATA_FILE));
            if (deleted) {
                Logger.debug("File deleted successfully: " + METADATA_FILE);
            } else {
                Logger.debug("File not found or could not be deleted.");
            }
        } catch (DirectoryNotEmptyException e) {
            Logger.error("Cannot delete non-empty directory: " + e.getMessage());
        } catch (IOException e) {
            // Catches other I/O errors, such as permission issues
            Logger.error("Error deleting file: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

package core.healing;

import core.platform.web.ChromeCustom;
import core.platform.utils.Logger;
import java.util.*;

public class VisualBridge {
    private final ChromeCustom driver;
    private final VisualDetector detector;

    public VisualBridge(ChromeCustom driver) {
        this.driver = driver;
        this.detector = new VisualDetector();
    }

    public List<SemanticLocator.ElementInfo> findElementsVisually(SemanticLocator semanticLocator) {
        Logger.info("Starting Visual AI scan of the page...");

        // 1. Capture screenshot
        byte[] screenshot = driver.screenshot();
        if (screenshot == null) {
            Logger.error("Failed to capture screenshot for Visual AI");
            return Collections.emptyList();
        }

        // 2. Run AI Detection
        List<VisualDetector.Detection> detections = detector.detect(screenshot);
        Logger.debug("Found %d potential UI elements via AI", detections.size());

        List<SemanticLocator.ElementInfo> results = new ArrayList<>();

        // 3. Map Coordinates to DOM Elements
        for (VisualDetector.Detection det : detections) {
            try {
                // Use document.elementFromPoint to get the element at the center of the
                // detection box
                // We use a helper script to get the outerHTML of the element at those
                // coordinates
                String js = String.format(
                        "var el = document.elementFromPoint(%d, %d); " +
                                "return el ? el.outerHTML : null;",
                        det.centerX(), det.centerY());

                Object result = driver.script(js);
                if (result instanceof String) {
                    String html = (String) result;
                    SemanticLocator.ElementInfo info = semanticLocator.extractElementInfo(html);
                    if (info != null) {
                        results.add(info);
                    }
                }
            } catch (Exception e) {
                Logger.warn("Failed to map coordinate (%d, %d) to DOM: %s", det.centerX(), det.centerY(),
                        e.getMessage());
            }
        }

        return results;
    }

    /**
     * Finds a specific element by comparing visual detections with an intent
     */
    public String findBestMatch(String intent) {
        // This is a simplified version. Real logic will be in SemanticLocator.
        return null;
    }
}

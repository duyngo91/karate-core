package core.healing;

import core.platform.utils.Logger;
import java.util.*;

public class SelfHealingDriver {
    private final IHealingDriver driver;
    private final LocatorHistory history;
    private final core.healing.engine.HealingEngine engine; // New V2 Engine

    public SelfHealingDriver(IHealingDriver driver) {
        this.driver = driver;
        this.history = new LocatorHistory();
        this.history.load();
        this.engine = new core.healing.engine.HealingEngine(); // Initialize Engine
    }

    public String findElement(String elementId, String originalLocator) {
        HealingMonitor monitor = HealingMonitor.getInstance();

        // Check if auto-healing is enabled
        if (!isAutoHealingEnabled()) {
            return tryLocator(originalLocator) ? originalLocator : null;
        }

        Logger.info("Self-healing search for: %s", elementId);

        // Try original locator
        if (tryLocator(originalLocator)) {
            history.recordSuccess(elementId, originalLocator);
            // No need to cache original locator if it works
            return originalLocator;
        }

        // Try learning-based prediction (V1 Memory)
        String learnedLocator = tryLearningBased(elementId);
        if (learnedLocator != null) {
            monitor.recordEvent(elementId, originalLocator, learnedLocator, "Learning", 1.0, true);
            HealingCache.getInstance().put(originalLocator, learnedLocator);
            return learnedLocator;
        }

        // Try V2 Healing Engine
        Logger.info("Invoking Healing Engine for: %s", elementId);
        core.healing.model.ElementNode targetNode = createTargetNode(elementId, originalLocator);
        core.healing.model.HealingResult result = engine.heal(driver, targetNode);

        if (result.isSuccess()) {
            String healedLocator = result.getElement().getLocator();
            if (healedLocator != null) {
                history.recordSuccess(elementId, healedLocator);
                history.save();

                // Update Global Cache
                HealingCache.getInstance().put(originalLocator, healedLocator);

                Logger.info("Healed using strategy %s: %s (Score: %.2f)",
                        result.getStrategyUsed(), healedLocator, result.getScore());
                monitor.recordEvent(elementId, originalLocator, healedLocator, result.getStrategyUsed(),
                        result.getScore(), true);
                return healedLocator;
            }
        }

        history.recordFailure(elementId, originalLocator);
        history.save();
        monitor.recordEvent(elementId, originalLocator, null, "None", 0.0, false);
        return null;
    }

    private core.healing.model.ElementNode createTargetNode(String elementId, String locator) {
        core.healing.model.ElementNode node = new core.healing.model.ElementNode();
        // 1. Enrich from ElementId (e.g. "login.username" -> name="username" or
        // text="username")
        String key = extractKey(elementId);
        node.addAttribute("name", key);
        node.addAttribute("id", key);
        node.addAttribute("data-testid", key);

        // 2. Parse Locator if possible (Simple extraction)
        if (locator != null && locator.startsWith("//")) {
            // Basic regex to pull tag and attributes
            // //input[@name='foo']
            try {
                int tagEnd = locator.indexOf("[");
                if (tagEnd > 2) {
                    String tag = locator.substring(2, tagEnd);
                    node.setTagName(tag);
                }

                // Extract attribute values
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("@([a-zA-Z-]+)='([^']*)'");
                java.util.regex.Matcher m = p.matcher(locator);
                while (m.find()) {
                    node.addAttribute(m.group(1), m.group(2));
                }

                // Extract text()
                if (locator.contains("text()")) {
                    java.util.regex.Pattern pt = java.util.regex.Pattern.compile("text\\(\\)\\s*=\\s*'([^']*)'");
                    java.util.regex.Matcher mt = pt.matcher(locator);
                    if (mt.find()) {
                        node.setText(mt.group(1));
                    }
                }

                // Extract Neighbor Info from XPath (e.g.
                // //label[text()='Password']/following-sibling::input)
                // This is a basic heuristc to infer context from the Locator string itself
                if (locator.contains("following-sibling")) {
                    java.util.regex.Pattern pNeighbor = java.util.regex.Pattern
                            .compile("\\[text\\(\\)\\s*=\\s*'([^']*)'\\].*following-sibling");
                    java.util.regex.Matcher mNeighbor = pNeighbor.matcher(locator);
                    if (mNeighbor.find()) {
                        String neighborText = mNeighbor.group(1);
                        node.setPrevSiblingText(neighborText);
                        Logger.debug("Inferred Neighbor Context: '%s'", neighborText);
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors, fallback to ID-based matching
            }
        }
        return node;
    }

    private String extractKey(String elementId) {
        if (elementId == null)
            return "";
        int dotIdx = elementId.lastIndexOf('.');
        return dotIdx >= 0 ? elementId.substring(dotIdx + 1) : elementId;
    }

    private String tryLearningBased(String elementId) {
        List<String> predicted = history.predictBestLocators(elementId);

        for (String locator : predicted) {
            if (tryLocator(locator)) {
                history.recordSuccess(elementId, locator);
                history.save();
                Logger.info("Healed using learning: %s", locator);
                return locator;
            }
        }
        return null;
    }

    private boolean tryLocator(String locator) {
        try {
            return driver.exist(locator);
        } catch (Exception e) {
            return false;
        }
    }

    public void click(String elementId, String locator) {
        String healedLocator = findElement(elementId, locator);
        if (healedLocator != null) {
            driver.click(healedLocator);
        } else {
            throw new RuntimeException("Element not found: " + elementId);
        }
    }

    public void input(String elementId, String locator, String value) {
        String healedLocator = findElement(elementId, locator);
        if (healedLocator != null) {
            driver.input(healedLocator, value);
        } else {
            throw new RuntimeException("Element not found: " + elementId);
        }
    }

    public String getText(String elementId, String locator) {
        String healedLocator = findElement(elementId, locator);
        if (healedLocator != null) {
            return driver.text(healedLocator);
        }
        throw new RuntimeException("Element not found: " + elementId);
    }

    private boolean isAutoHealingEnabled() {
        return HealingConfig.getInstance().isEnabled();
    }
}

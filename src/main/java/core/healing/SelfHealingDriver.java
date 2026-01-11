package core.healing;

import core.platform.utils.Logger;

public class SelfHealingDriver {
    private final IHealingDriver driver;
    // private final LocatorHistory history; // Removed in favor of
    // GoldenStateRecorder
    private final core.healing.engine.HealingEngine engine; // New V2 Engine

    public SelfHealingDriver(IHealingDriver driver) {
        this.driver = driver;
        // this.history = new LocatorHistory();
        // this.history.load();
        this.engine = new core.healing.engine.HealingEngine(); // Initialize Engine
        // Ensure recorder is loaded
        core.healing.rag.GoldenStateRecorder.getInstance();
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
            // history.recordSuccess(elementId, originalLocator); // Removed
            if (HealingConfig.getInstance().isCaptureGoldenState()) {
                core.healing.rag.GoldenStateRecorder.getInstance().captureAndSave(driver, elementId, originalLocator);
            }
            // No need to cache original locator if it works
            return originalLocator;
        }

        // Try learning-based prediction (Golden State Cache)
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

                if (HealingConfig.getInstance().isCaptureGoldenState()) {
                    core.healing.rag.GoldenStateRecorder.getInstance().captureAndSave(driver, elementId, healedLocator);
                }

                // Update Global Cache
                HealingCache.getInstance().put(originalLocator, healedLocator);

                Logger.info("Healed using strategy %s: %s (Score: %.2f)",
                        result.getStrategyUsed(), healedLocator, result.getScore());
                monitor.recordEvent(elementId, originalLocator, healedLocator, result.getStrategyUsed(),
                        result.getScore(), true);
                return healedLocator;
            }
        }

        monitor.recordEvent(elementId, originalLocator, null, "None", 0.0, false);
        return null;
    }

    private core.healing.model.ElementNode createTargetNode(String elementId, String locator) {
        core.healing.model.ElementNode node = new core.healing.model.ElementNode();
        node.setElementId(elementId);

        // 1. Enrich from ElementId (e.g. "login.username" -> name="username" or
        // text="username")
        String key = extractKey(elementId);
        node.addAttribute("name", key);
        node.addAttribute("id", key);
        node.addAttribute("data-testid", key);

        // 2. Parse Locator if possible (Robust segment-based extraction)
        if (locator != null && (locator.startsWith("//") || locator.startsWith("/"))) {
            try {
                // Split into segments and take the last one
                String[] segments = locator.split("/+");
                String lastSegment = "";
                for (int i = segments.length - 1; i >= 0; i--) {
                    if (!segments[i].trim().isEmpty()) {
                        lastSegment = segments[i].trim();
                        break;
                    }
                }

                if (!lastSegment.isEmpty()) {
                    // Handle axes like following-sibling::input
                    if (lastSegment.contains("::")) {
                        lastSegment = lastSegment.substring(lastSegment.indexOf("::") + 2);
                    }

                    // Extract tag
                    int bracketIdx = lastSegment.indexOf("[");
                    String tag = bracketIdx > 0 ? lastSegment.substring(0, bracketIdx) : lastSegment;
                    if (!tag.isEmpty() && tag.matches("[a-zA-Z0-9]+")) {
                        node.setTagName(tag);
                    }

                    // Extract all @attributes in this segment
                    java.util.regex.Pattern pAttr = java.util.regex.Pattern.compile("@([a-zA-Z0-9-]+)='([^']*)'");
                    java.util.regex.Matcher mAttr = pAttr.matcher(lastSegment);
                    while (mAttr.find()) {
                        node.addAttribute(mAttr.group(1), mAttr.group(2));
                    }

                    // Extract text() in this segment
                    java.util.regex.Pattern pText = java.util.regex.Pattern.compile("text\\(\\)\\s*=\\s*'([^']*)'");
                    java.util.regex.Matcher mText = pText.matcher(lastSegment);
                    if (mText.find()) {
                        node.setText(mText.group(1));
                    }

                    // If simple text match like [text()='foo'], this is also a label sign
                    if (node.getText() != null && node.getTagName().equals("button")) {
                        node.addAttribute("name", node.getText());
                    }
                }

                // Global attribute sweep (fallback)
                java.util.regex.Pattern pAll = java.util.regex.Pattern.compile("@([a-zA-Z0-9-]+)='([^']*)'");
                java.util.regex.Matcher mAll = pAll.matcher(locator);
                while (mAll.find()) {
                    if (!node.getAttributes().containsKey(mAll.group(1))) {
                        node.addAttribute(mAll.group(1), mAll.group(2));
                    }
                }

                // Neighbor Context
                if (locator.contains("following")) {
                    java.util.regex.Pattern pNeighbor = java.util.regex.Pattern.compile("\\[text\\(\\)\\s*=\\s*'([^']*)'\\]");
                    java.util.regex.Matcher mNeighbor = pNeighbor.matcher(locator);
                    if (mNeighbor.find()) {
                        node.setPrevSiblingText(mNeighbor.group(1));
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        Logger.info("[TargetNode] ID: %s, Key: %s, Tag: %s, Attrs: %s", elementId, extractKey(elementId), node.getTagName(), node.getAttributes());
        return node;
    }

    private String extractKey(String elementId) {
        if (elementId == null)
            return "";
        int dotIdx = elementId.lastIndexOf('.');
        return dotIdx >= 0 ? elementId.substring(dotIdx + 1) : elementId;
    }

    private String tryLearningBased(String elementId) {
        if (!HealingConfig.getInstance().isRagEnabled()) {
            return null;
        }
        // Use GoldenStateRecorder as the persistent cache
        core.healing.rag.ElementMetadata meta = core.healing.rag.GoldenStateRecorder.getInstance()
                .getMetadata(elementId);
        if (meta != null && meta.getLocator() != null) {
            String cachedLocator = meta.getLocator();
            if (tryLocator(cachedLocator)) {
                Logger.info("Healed using Golden State Cache: %s", cachedLocator);
                return cachedLocator;
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

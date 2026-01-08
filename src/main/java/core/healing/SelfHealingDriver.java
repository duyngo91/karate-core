package core.healing;

import core.platform.web.ChromeCustom;
import core.platform.utils.Logger;
import java.util.*;

public class SelfHealingDriver {
    private final ChromeCustom driver;
    private final SemanticLocator semanticLocator;
    private final LocatorHistory history;
    
    public SelfHealingDriver(ChromeCustom driver) {
        this.driver = driver;
        this.semanticLocator = new SemanticLocator(driver);
        this.history = new LocatorHistory();
        this.history.load();
    }
    
    public String findElement(String elementId, String originalLocator) {
        // Check if auto-healing is enabled
        if (!isAutoHealingEnabled()) {
            return tryLocator(originalLocator) ? originalLocator : null;
        }
        
        Logger.info("Self-healing search for: %s", elementId);
        
        // Try original locator
        if (tryLocator(originalLocator)) {
            history.recordSuccess(elementId, originalLocator);
            return originalLocator;
        }
        
        // Try learning-based prediction
        String learnedLocator = tryLearningBased(elementId);
        if (learnedLocator != null) return learnedLocator;
        
        // Try semantic locators
        String semanticLoc = semanticLocator.findBySemantics(elementId);
        if (semanticLoc != null) {
            history.recordSuccess(elementId, semanticLoc);
            history.save();
            Logger.info("Healed using semantic: %s", semanticLoc);
            return semanticLoc;
        }
        
        history.recordFailure(elementId, originalLocator);
        history.save();
        return null;
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
        String property = System.getProperty("auto.healing");
        return "true".equalsIgnoreCase(property);
    }
}

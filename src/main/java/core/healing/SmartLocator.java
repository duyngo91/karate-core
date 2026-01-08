package core.healing;

import core.platform.web.ChromeCustom;
import core.platform.utils.Logger;

public class SmartLocator {
    private final ChromeCustom driver;
    private final LocatorRepository repository;
    private final SelfHealingDriver healer;
    private boolean autoUpdate = true;
    
    public SmartLocator(ChromeCustom driver, String locatorFilePath) {
        this.driver = driver;
        this.repository = LocatorRepository.getInstance();
        this.repository.loadFromFile(locatorFilePath);
        this.healer = new SelfHealingDriver(driver);
    }
    
    public SmartLocator(ChromeCustom driver, String locatorJson, boolean fromJson) {
        this.driver = driver;
        this.repository = LocatorRepository.getInstance();
        if (fromJson) {
            this.repository.loadFromJson(locatorJson);
        }
        this.healer = new SelfHealingDriver(driver);
    }
    
    public void click(String page, String element) {
        String locator = repository.getLocator(page, element);
        String elementId = repository.getElementId(page, element);
        
        String healedLocator = healer.findElement(elementId, locator);
        
        if (healedLocator != null) {
            if (!healedLocator.equals(locator) && autoUpdate) {
                Logger.info("Auto-updating locator: %s.%s", page, element);
                repository.updateLocator(page, element, healedLocator);
            }
            driver.click(healedLocator);
        } else {
            throw new RuntimeException("Element not found: " + elementId);
        }
    }
    
    public void input(String page, String element, String value) {
        String locator = repository.getLocator(page, element);
        String elementId = repository.getElementId(page, element);
        
        String healedLocator = healer.findElement(elementId, locator);
        
        if (healedLocator != null) {
            if (!healedLocator.equals(locator) && autoUpdate) {
                repository.updateLocator(page, element, healedLocator);
            }
            driver.input(healedLocator, value);
        } else {
            throw new RuntimeException("Element not found: " + elementId);
        }
    }
    
    public String getText(String page, String element) {
        String locator = repository.getLocator(page, element);
        String elementId = repository.getElementId(page, element);
        
        String healedLocator = healer.findElement(elementId, locator);
        
        if (healedLocator != null) {
            if (!healedLocator.equals(locator) && autoUpdate) {
                repository.updateLocator(page, element, healedLocator);
            }
            return driver.text(healedLocator);
        }
        throw new RuntimeException("Element not found: " + elementId);
    }
    
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
    
    public void saveLocators(String filePath) {
        repository.saveToFile(filePath);
    }
}

package core.platform.web.element;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.DriverOptions;

public class ShadowElement {
    private Driver driver;
    private String hostSelector;
    private String elementSelector;

    public ShadowElement(Driver driver, String hostSelector, String elementSelector) {
        this.driver = driver;
        this.hostSelector = hostSelector;
        this.elementSelector = elementSelector;
    }

    private String getElementScript(String action) {
        return String.format(
            "var host = %s;" +
            "var shadow = host.shadowRoot;" +
            "var element = %s;" +
            "%s",
                DriverOptions.selector(hostSelector, "document"), DriverOptions.selector(elementSelector, "shadow"),
                action
        );
    }

    public ShadowElement click() {
        driver.script(getElementScript("element.click();"));
        return this;
    }

    public ShadowElement input(String value) {
        driver.script(getElementScript(
                String.format("element.value = '%s'; element.dispatchEvent(new Event('input', {bubbles: true}));", value)));
        return this;
    }

    public String text() {
        return driver.script(getElementScript("element.textContent;")).toString();
    }

    public String value() {
        return driver.script(getElementScript("element.value;")).toString();
    }

    public boolean isDisplayed() {
        return (Boolean) driver.script(getElementScript("element && element.offsetParent !== null;"));
    }

    public ShadowElement clear() {
        driver.script(getElementScript("element.value = '';"));
        return this;
    }
}
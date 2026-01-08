package core.platform.web.element;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.DriverOptions;
import com.intuit.karate.driver.Element;
import net.minidev.json.JSONArray;

import java.util.Map;

public class WebElement extends AbsElement {

    public WebElement(Driver driver, String locator) {
        this.driver = driver;
        this.locator = locator;
    }

    public WebElement(Driver driver, Element element) {
        this.driver = driver;
        this.element = element;
    }


    public WebElement getWebElement() {
        return new WebElement(driver, getElement());
    }

    public int count() {
        JSONArray array = null;
        try {
            array = (JSONArray) scriptAll(getLocator(), "_.children.length");
        } catch (Exception ignored) {
            return 0;
        }
        return array == null ? 0 : array.size();
    }


    public String outerHTML() {
        String temp = "(function() { var e = " + DriverOptions.selector(getLocator()) + ";\n" + "return e.outerHTML" + "})();";
        Object value = script(temp);
        return value == null ? null : (String) value;
    }

    public String innerHTML() {
        String temp = "(function() { var e = " + DriverOptions.selector(getLocator()) + ";\n" + "return e.innerHTML" + "})();";
        Object value = script(temp);
        return value == null ? null : (String) value;
    }


    public Element clickAll() {
        getElements().forEach(e -> {
            e.click();
            delay(500);
        });
        return this;
    }


    public boolean isVisible() {
        Map<String, Object> position = getPosition();
        return isPresent() && !(
                position.get("x").equals(0) &&
                position.get("y").equals(0) &&
                position.get("width").equals(0) &&
                position.get("height").equals(0)
        );
    }


    public String textContent() {
        return (String) driver.script(getLocator(), "_.textContent");
    }

}

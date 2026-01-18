package core.platform.web.element;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;
import com.intuit.karate.driver.Finder;
import com.intuit.karate.driver.Mouse;

import java.util.List;
import java.util.Map;

public abstract class AbsElement implements Element {

    protected Element element;
    protected Driver driver;
    protected String locator;

    public Element getElement() {
        if (element != null) {
            return element;
        } else {
            return driver.locate(locator);
        }
    }


    public List<Element> getElements() {
        String locate;
        if (element != null) {
            locate = element.getLocator();
        } else {
            locate = locator;
        }
//        return driver.locateAll(locate)
//                .stream().map(x -> new WebElement(driver, x.getLocator()))
//                .collect(Collectors.toList());
        return null;
    }


    @Override
    public String getLocator() {
        if (element != null) {
            return element.getLocator();
        } else {
            return locator;
        }
    }

    @Override
    public boolean isPresent() {
        return getElement().isPresent();
    }

    @Override
    public boolean isEnabled() {
        return getElement().isEnabled();
    }

    @Override
    public Map<String, Object> getPosition() {
        return getElement().getPosition();
    }

    @Override
    public byte[] screenshot() {
        return getElement().screenshot();
    }

    @Override
    public Element highlight() {
        return getElement().highlight();
    }

    @Override
    public Element focus() {
        return getElement().focus();
    }

    @Override
    public Element clear() {
        return getElement().clear();
    }

    @Override
    public Element click() {
        return getElement().click();
    }

    @Override
    public Element submit() {
        return getElement().submit();
    }

    @Override
    public Element scroll() {
        return getElement().scroll();
    }

    @Override
    public Mouse mouse() {
        return getElement().mouse();
    }

    @Override
    public Element input(String value) {
        return getElement().input(value);
    }

    @Override
    public Element input(String[] values) {
        return getElement().input(values);
    }

    @Override
    public Element input(String[] values, int delay) {
        return getElement().input(values, delay);
    }

    @Override
    public Element select(String text) {
        return getElement().focus().select(text);
    }

    @Override
    public Element select(int index) {
        return getElement().focus().select(index);
    }

    @Override
    public Element switchFrame() {
        return getElement().switchFrame();
    }

    @Override
    public Element delay(int millis) {
        return getElement().delay(millis);
    }

    @Override
    public Element retry() {
        return getElement().retry();
    }

    @Override
    public Element retry(int count) {
        return getElement().retry(count);
    }

    @Override
    public Element retry(Integer count, Integer interval) {
        return getElement().retry(count, interval);
    }


    @Override
    public Element waitFor() {
        return getElement().waitFor();
    }

    @Override
    public Element waitUntil(String expression) {
        return getElement().waitUntil(expression);
    }

    @Override
    public Element waitForText(String text) {
        return getElement().waitForText(text);
    }

    @Override
    public Object script(String expression) {
        return driver.script(expression);
    }

    @Override
    public Object scriptAll(String locator, String expression) {
        return driver.scriptAll(locator, expression);
    }

    @Override
    public Element optional(String locator) {
        return driver.optional(locator);
    }

    @Override
    public boolean exists(String locator) {
        return driver.exists(locator);
    }

    @Override
    public Element locate(String locator) {
        return driver.locate(locator);
    }

    @Override
    public List<Element> locateAll(String locator) {
        return driver.locateAll(locator);
    }

    @Override
    public String getHtml() {
        return getElement().getHtml();
    }

    @Override
    public void setHtml(String html) {
        getElement().setHtml(html);
    }

    @Override
    public String getText() {
        return getElement().getText();
    }

    @Override
    public void setText(String text) {
        getElement().setText(text);
    }

    @Override
    public String getValue() {
        return getElement().getValue();
    }

    @Override
    public void setValue(String value) {
        getElement().setValue(value);
    }

    @Override
    public String attribute(String name) {
        return getElement().attribute(name);
    }

    @Override
    public String property(String name) {
        return getElement().property(name);
    }

    @Override
    public Element getParent() {
        return getElement().getParent();
    }

    @Override
    public Element getFirstChild() {
        return getElement().getFirstChild();
    }

    @Override
    public Element getLastChild() {
        return getElement().getLastChild();
    }

    @Override
    public Element getPreviousSibling() {
        return getElement().getPreviousSibling();
    }

    @Override
    public Element getNextSibling() {
        return getElement().getNextSibling();
    }

    @Override
    public List<Element> getChildren() {
        return getElement().getChildren();
    }

    @Override
    public Finder rightOf() {
        return getElement().rightOf();
    }

    @Override
    public Finder leftOf() {
        return getElement().leftOf();
    }

    @Override
    public Finder above() {
        return getElement().above();
    }

    @Override
    public Finder below() {
        return getElement().below();
    }

    @Override
    public Finder near() {
        return getElement().near();
    }





}

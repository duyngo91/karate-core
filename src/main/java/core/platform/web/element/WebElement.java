package core.platform.web.element;

import com.intuit.karate.driver.*;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.Map;

public class WebElement implements Element  {


    protected final Driver driver;
    private Element delegate;
    private String locator;

    public WebElement(Driver driver, String locator) {
        this.driver = driver;
        this.locator = locator;
    }
    public WebElement(Driver driver, Element delegate) {
        this.driver = driver;
        this.delegate = delegate;
    }

    public Element getElement() {
        if (delegate != null) {
            return delegate;
        } else {
            return driver.locate(locator);
        }
    }

    @Override
    public String getLocator() {
        return delegate.getLocator();
    }

    @Override
    public boolean isPresent() {
        return delegate.isPresent();
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public Map<String, Object> getPosition() {
        return delegate.getPosition();
    }

    @Override
    public byte[] screenshot() {
        return delegate.screenshot();
    }

    @Override
    public Element highlight() {
        return delegate.highlight();
    }

    @Override
    public Element focus() {
        return delegate.focus();
    }

    @Override
    public Element clear() {
        return delegate.clear();
    }

    @Override
    public Element click() {
        return delegate.click();
    }

    @Override
    public Element submit() {
        return delegate.submit();
    }

    @Override
    public Element scroll() {
        return delegate.scroll();
    }

    @Override
    public Mouse mouse() {
        return delegate.mouse();
    }

    @Override
    public Element input(String value) {
        return delegate.input(value);
    }

    @Override
    public Element input(String[] values) {
        return delegate.input(values);
    }

    @Override
    public Element input(String[] values, int delay) {
        return delegate.input(values, delay);
    }

    @Override
    public Element select(String text) {
        return delegate.select(text);
    }

    @Override
    public Element select(int index) {
        return delegate.select(index);
    }

    @Override
    public Element switchFrame() {
        return delegate.switchFrame();
    }

    @Override
    public Element delay(int millis) {
        return delegate.delay(millis);
    }

    @Override
    public Element retry() {
        return delegate.retry();
    }

    @Override
    public Element retry(int count) {
        return delegate.retry(count);
    }

    @Override
    public Element retry(Integer count, Integer interval) {
        return delegate.retry(count, interval);
    }

    @Override
    public Element waitFor() {
        return delegate.waitFor();
    }

    @Override
    public Element waitUntil(String expression) {
        return delegate.waitUntil(expression);
    }

    @Override
    public Element waitForText(String text) {
        return delegate.waitForText(text);
    }

    @Override
    public Object script(String expression) {
        return delegate.script(expression);
    }

    @Override
    public Object scriptAll(String locator, String expression) {
        return delegate.scriptAll(locator, expression);
    }

    @Override
    public Element optional(String locator) {
        return delegate.optional(locator);
    }

    @Override
    public boolean exists(String locator) {
        return delegate.exists(locator);
    }

    @Override
    public Element locate(String locator) {
        return delegate.locate(locator);
    }

    @Override
    public List<Element> locateAll(String locator) {
        return delegate.locateAll(locator);
    }

    @Override
    public String getHtml() {
        return delegate.getHtml();
    }

    @Override
    public void setHtml(String html) {
        delegate.setHtml(html);
    }

    @Override
    public String getText() {
        return delegate.getText();
    }

    @Override
    public void setText(String text) {
        delegate.setText(text);
    }

    @Override
    public String getValue() {
        return delegate.getText();
    }

    @Override
    public void setValue(String value) {
        delegate.setValue(value);
    }

    @Override
    public String attribute(String name) {
        return delegate.attribute(name);
    }

    @Override
    public String property(String name) {
        return delegate.property(name);
    }

    @Override
    public Element getParent() {
        return delegate.getParent();
    }

    @Override
    public Element getFirstChild() {
        return delegate.getFirstChild();
    }

    @Override
    public Element getLastChild() {
        return delegate.getLastChild();
    }

    @Override
    public Element getPreviousSibling() {
        return delegate.getPreviousSibling();
    }

    @Override
    public Element getNextSibling() {
        return delegate.getNextSibling();
    }

    @Override
    public List<Element> getChildren() {
        return delegate.getChildren();
    }

    @Override
    public Finder rightOf() {
        return delegate.rightOf();
    }

    @Override
    public Finder leftOf() {
        return delegate.leftOf();
    }

    @Override
    public Finder above() {
        return delegate.above();
    }

    @Override
    public Finder below() {
        return delegate.below();
    }

    @Override
    public Finder near() {
        return delegate.near();
    }
}

package core.healing.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Framework-agnostic representation of a DOM element.
 * Decouples logic from Selenium WebElement or other driver-specific objects.
 */
public class ElementNode {
    private String tagName;
    private String text;
    private Map<String, String> attributes = new HashMap<>();
    private String locator; // The locator used to find this (if known) or constructed
    private int x, y, width, height;
    private int depth; // Hierarchy depth if available
    private String parentTag; // Tag of the parent element

    public ElementNode() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public void addAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getParentTag() {
        return parentTag;
    }

    public void setParentTag(String parentTag) {
        this.parentTag = parentTag;
    }

    @Override
    public String toString() {
        return String.format("<%s> %s %s", tagName, attributes, text != null ? text : "");
    }
}

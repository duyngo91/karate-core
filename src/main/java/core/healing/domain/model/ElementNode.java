package core.healing.domain.model;

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
    private String prevSiblingTag; // Tag of the previous sibling
    private String prevSiblingText; // Text of the previous sibling
    private String elementId; // Unique ID for RAG lookup (e.g. "login.username")
    private int domIndex; // Unique DOM index/path if available
    private String formId; // Unique form/container ID if available
    private String structuralPath; // Ancestor tag chain (e.g. "button > div > form")

    // Transient field for Visual Healing (not serialized, in-memory only)
    private transient java.awt.image.BufferedImage screenshot;

    public ElementNode() {
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public int getDomIndex() {
        return domIndex;
    }

    public void setDomIndex(int domIndex) {
        this.domIndex = domIndex;
    }

    public String getStructuralPath() {
        return structuralPath;
    }

    public void setStructuralPath(String structuralPath) {
        this.structuralPath = structuralPath;
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

    public String getPrevSiblingTag() {
        return prevSiblingTag;
    }

    public void setPrevSiblingTag(String prevSiblingTag) {
        this.prevSiblingTag = prevSiblingTag;
    }

    public String getPrevSiblingText() {
        return prevSiblingText;
    }

    public void setPrevSiblingText(String prevSiblingText) {
        this.prevSiblingText = prevSiblingText;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public java.awt.image.BufferedImage getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(java.awt.image.BufferedImage screenshot) {
        this.screenshot = screenshot;
    }

    public void genLocator() {
        this.setLocator(constructLocator(this));
    }


    @Override
    public String toString() {
        return String.format("<%s> %s %s", tagName, attributes, text != null ? text : "");
    }

    private String constructLocator(ElementNode node) {
        // Try id
        String id = node.getAttribute("id");
        if (id != null && !id.isEmpty())
            return "//*[@id='" + id + "']";

        // Try name
        String name = node.getAttribute("name");
        if (name != null && !name.isEmpty())
            return "//*[@name='" + name + "']";

        // Try text
        if (node.getText() != null && !node.getText().isEmpty()) {
            // Simplified text locator
            String text = node.getText().length() > 20 ? node.getText().substring(0, 20) : node.getText();
            return "//" + node.getTagName() + "[contains(., '" + text + "')]";
        }

        // Try Neighbor Context (if available and strong)
        if (node.getPrevSiblingText() != null && !node.getPrevSiblingText().isEmpty()
                && node.getPrevSiblingTag() != null) {
            // Heuristic: If we are here, ID/Name/Text strategies likely failed.
            // Construct: //prevTag[contains(.,'prevText')]/following-sibling::tag[1]
            String prevText = node.getPrevSiblingText().length() > 20 ? node.getPrevSiblingText().substring(0, 20)
                    : node.getPrevSiblingText();
            // Escape single quotes just in case
            prevText = prevText.replace("'", "\'");

            return "//" + node.getPrevSiblingTag() + "[contains(., '" + prevText + "')]/following-sibling::"
                    + node.getTagName() + "[1]";
        }

        // Fallback to internal xpath if we had one, or attributes
        for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            if (!entry.getKey().equals("class") && !entry.getKey().equals("style")) {
                return "//" + node.getTagName() + "[@" + entry.getKey() + "='" + entry.getValue() + "']";
            }
        }

        return "//" + node.getTagName(); // Weak fallback
    }

    /**
     * Suy luận role của element dựa trên tag + nội dung
     */
    public ElementRole inferRole() {

        if (tagName == null) {
            return ElementRole.UNKNOWN;
        }

        switch (tagName) {
            case "input":
            case "textarea":
            case "select":
                return ElementRole.INPUT;

            case "label":
                return ElementRole.LABEL;

            case "button":
                return ElementRole.BUTTON;

            case "a":
                return ElementRole.LINK;

            case "div":
            case "section":
            case "article":
            case "form":
                return ElementRole.CONTAINER;
        }

        if (text != null && !text.trim().isEmpty()) {
            return ElementRole.TEXT;
        }

        return ElementRole.UNKNOWN;
    }
}

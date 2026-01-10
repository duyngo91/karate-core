package core.healing.rag;

import java.util.Map;

/**
 * Represents the comprehensive state of an element ("Golden Snapshot").
 * Used for RAG-based healing to match against candidates using embeddings.
 */
public class ElementMetadata {
    private String elementId; // e.g., "login.username"
    private String locator;
    private String tagName;
    private String text;
    private Map<String, String> attributes;
    private String neighborText; // Text of the previous sibling
    private float[] vector; // Embedding vector (Dimension 384 for MiniLM-L6)

    public ElementMetadata() {
    }

    public ElementMetadata(String elementId, String locator, String tagName, String text,
            Map<String, String> attributes, String neighborText) {
        this.elementId = elementId;
        this.locator = locator;
        this.tagName = tagName;
        this.text = text;
        this.attributes = attributes;
        this.neighborText = neighborText;
    }

    // Getters and Setters
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
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

    public String getNeighborText() {
        return neighborText;
    }

    public void setNeighborText(String neighborText) {
        this.neighborText = neighborText;
    }

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }
}

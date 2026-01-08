package core.platform.exceptions;

public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String locator) {
        super("Element not found with locator: " + locator);
    }
    
    public ElementNotFoundException(String locator, Throwable cause) {
        super("Element not found with locator: " + locator, cause);
    }
}
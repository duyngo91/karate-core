package core.platform.common;

public final class Constants {
    
    // Timeouts
    public static final int DEFAULT_TIMEOUT = 10000;
    public static final int SHORT_TIMEOUT = 5000;
    public static final int LONG_TIMEOUT = 60000;
    public static final int DEFAULT_RETRY = 500;
    public static final int NO_RETRY = 0;
    
    // Scroll values
    public static final int MIN_SCROLL_POINT = 0;
    public static final int MAX_SCROLL_POINT = 99999;
    
    // Mobile constants
    public static final int MOBILE_TIMEOUT = 10000;
    public static final int MOBILE_RETRY = 500;
    public static final int LONG_CLICK_DURATION = 2000;
    public static final String SWIPE_SPEED = "500";
    
    // Network constants
    public static final String NETWORK_REQUEST_WILL_BE_SENT = "Network.requestWillBeSent";
    public static final String NETWORK_RESPONSE_RECEIVED = "Network.responseReceived";
    public static final String NETWORK_RESPONSE_BODY = "Network.getResponseBody";
    public static final String NETWORK_REQUEST_BODY = "Network.getRequestPostData";
    
    // Element types
    public static final String POINTER_TYPE_TOUCH = "touch";
    public static final String POINTER_TYPE_MOUSE = "mouse";
    
    // Action types
    public static final String POINTER_MOVE = "pointerMove";
    public static final String POINTER_DOWN = "pointerDown";
    public static final String POINTER_UP = "pointerUp";
    public static final String PAUSE = "pause";
    
    // File paths
    public static final String DEFAULT_DOWNLOAD_PATH = "file:target";
    
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
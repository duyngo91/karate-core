package core.platform.mobile;

import com.google.gson.Gson;
import com.intuit.karate.core.AutoDef;
import com.intuit.karate.core.Plugin;
import com.intuit.karate.core.ScenarioEngine;
import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.DriverElement;
import com.intuit.karate.driver.Element;
import com.intuit.karate.driver.appium.AppiumDriver;
import com.intuit.karate.driver.appium.MobileDriverOptions;
import core.platform.common.Configuration;
import core.platform.common.Constants;
import core.platform.common.DriverType;
import core.platform.exceptions.ElementNotFoundException;
import core.platform.manager.DriverManager;
import core.platform.mobile.models.Actions;
import core.platform.mobile.models.ListActions;
import core.platform.utils.Logger;
import core.platform.web.wait.Wait;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class MobileCustom extends AppiumDriver {
    private static final int TIMEOUT = Constants.MOBILE_TIMEOUT;
    private static final int RETRY_TIME = Constants.MOBILE_RETRY;
    private static final int LONG_CLICK_DURATION = Constants.LONG_CLICK_DURATION;
    private static final String ELEMENT = "element";
    private static final String PARAMS = "params";
    private static final String APPIUM = "appium";
    private static final String APP = "app";
    private static final String DEVICE = "device";
    private static final String TOUCH = "touch";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String VALUE_KEY = "value";
    private static final String FINGER1 = "finger1";
    private static final String POINTER = "pointer";
    private static final String POINTER_MOVE = "pointerMove";
    private static final String POINTER_DOWN = "pointerDown";
    private static final String PAUSE = "pause";
    private static final String POINTER_UP = "pointerUp";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String SPEED = "speed";

    protected MobileCustom(MobileDriverOptions options) {
        super(options);
    }


    public Element E(String locator) {
        return DriverElement.locatorExists(this, locator);
    }

    @Override
    public void activate() {
        super.setContext("NATIVE_APP");
    }

    @Override
    public List<String> methodNames() {
        List<String> methods = super.methodNames();
        methods.addAll(Plugin.methodNames(MobileCustom.class));
        return methods;
    }

    @AutoDef
    public int windowHeight() {
        return (int) getDimensions().get(HEIGHT);
    }

    @AutoDef
    public int windowWidth() {
        return (int) getDimensions().get(WIDTH);
    }

    @AutoDef
    public Element scrollDownToView(String locator) {
        scrollDownToView(Configuration.getLongTimeout(), Constants.NO_RETRY, locator);
        return E(locator);
    }

    @AutoDef
    public Element scrollDownToView(int timeOutMiliSecond, int retry, String locator) {
        int windowHeight = windowHeight();
        int windowWidth = windowWidth();
        Wait.until(timeOutMiliSecond, retry, () -> {
            List<String> elements = elementIds(locator);
            if (elements.size() > 0 && isDisplayed(locator)) {
                return true;
            }
            swipeV2(windowWidth / 2, windowHeight * 7 / 10, windowWidth / 2, windowHeight * 3 / 10);
            return false;
        });
        return E(locator);
    }

    @AutoDef
    public MobileCustom scrollToTop() {
        int windowHeight = windowHeight();
        swipeV2(1, windowHeight * 8 / 10, 1, windowHeight * 2 / 10);
        return this;
    }

    @AutoDef
    public MobileCustom scrollToBottom() {
        int windowHeight = windowHeight();
        swipeV2(1, windowHeight * 2 / 10, 1, windowHeight * 8 / 10);
        return this;
    }

    @AutoDef
    public MobileCustom swipeV2(Object fromX, Object fromY, Object toX, Object toY) {
        ListActions list = ListActions.init().add(
                Actions.builder()
                        .id(FINGER1)
                        .type(POINTER)
                        .buildParameters().setPointerType(TOUCH)
                        .buildAction().setType(POINTER_MOVE).setDuration(0).setX(fromX).setY(fromY)
                        .buildAction().setType(POINTER_DOWN).setButton(0)
                        .buildAction().setType(POINTER_MOVE).setDuration(1000).setX(toX).setY(toY)
                        .buildAction().setType(POINTER_UP).setButton(0).done());

        actions(new Gson().toJson(list));
        return this;
    }

    @AutoDef
    public MobileCustom longClick(String locator) {
        longClick(locator, LONG_CLICK_DURATION);
        return this;
    }

    @AutoDef
    public MobileCustom longClick(String locator, int duration) {
        Logger.debug("Long clicking element: %s (duration: %dms)", locator, duration);

        if (!isDisplayed(locator)) {
            throw new ElementNotFoundException(locator);
        }

        Map<String, Object> location = getLocation(locator);
        Map<String, Object> size = getSize(locator);

        if (location.isEmpty() || size.isEmpty()) {
            throw new ElementNotFoundException("Cannot get location/size for: " + locator);
        }

        int x = (int) location.get(X);
        int y = (int) location.get(Y);
        int height = (int) size.get(HEIGHT);
        int width = (int) size.get(WIDTH);

        longClick((x + width / 2), (y + height / 2), duration);
        Logger.debug("Long click completed for: %s", locator);
        return this;
    }

    @AutoDef
    public MobileCustom longClick(Object fromX, Object fromY, int duration) {
        ListActions list = ListActions.init().add(
                Actions.builder()
                        .id(FINGER1)
                        .type(POINTER)
                        .buildParameters().setPointerType(TOUCH)
                        .buildAction().setType(POINTER_MOVE).setDuration(0).setX(fromX).setY(fromY)
                        .buildAction().setType(POINTER_DOWN).setButton(0)
                        .buildAction().setType(PAUSE).setDuration(duration)
                        .buildAction().setType(POINTER_UP).setButton(0).done());

        actions(new Gson().toJson(list));
        return this;
    }

    @AutoDef
    public boolean isDisplayed(String locator) {
        Logger.debug("Checking if element is displayed: %s", locator);
        try {
            String id = elementId(locator);
            boolean result = http.path(ELEMENT, id, "displayed").get().json().get(VALUE_KEY);
            Logger.debug("Element %s displayed: %s", locator, result);
            return result;
        } catch (Exception e) {
            Logger.warn("Failed to check if element is displayed: %s - %s", locator, e.getMessage());
            return false;
        }
    }

    @AutoDef
    public boolean isEnabled(String locator) {
        Logger.debug("Checking if element is enabled: %s", locator);
        try {
            String id = elementId(locator);
            boolean result = http.path(ELEMENT, id, "enabled").get().json().get(VALUE_KEY);
            Logger.debug("Element %s enabled: %s", locator, result);
            return result;
        } catch (Exception e) {
            Logger.warn("Failed to check if element is enabled: %s - %s", locator, e.getMessage());
            return false;
        }
    }

    @AutoDef
    public String getText(String locator) {
        Logger.debug("Getting text from element: %s", locator);
        try {
            String id = elementId(locator);
            String result = http.path(ELEMENT, id, "text").get().json().get(VALUE_KEY);
            Logger.debug("Text from %s: '%s'", locator, result);
            return result;
        } catch (Exception e) {
            Logger.warn("Failed to get text from element: %s - %s", locator, e.getMessage());
            return "";
        }
    }

    @AutoDef
    public List<String> getAllText(String locator) {
        return elementIds(locator)
                .stream()
                .map(x -> (String) http.path(ELEMENT, x, "text").get().json().get(VALUE_KEY))
                .collect(Collectors.toList());
    }

    @AutoDef
    public Map<String, Object> getLocation(String locator) {
        try {
            String id = elementId(locator);
            return http.path(ELEMENT, id, "location").get().json().get(VALUE_KEY);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @AutoDef
    public Map<String, Object> getSize(String locator) {
        try {
            String id = elementId(locator);
            return http.path(ELEMENT, id, "size").get().json().get(VALUE_KEY);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @AutoDef
    public Map<String, Object> getElementRect(String locator) {
        Logger.debug("Getting element rect: %s", locator);
        try {
            String id = elementId(locator);
            Map<String, Object> location = http.path(ELEMENT, id, "location").get().json().get(VALUE_KEY);
            Map<String, Object> size = http.path(ELEMENT, id, "size").get().json().get(VALUE_KEY);
            
            Map<String, Object> rect = new HashMap<>(location);
            rect.putAll(size);
            return rect;
        } catch (Exception e) {
            Logger.warn("Failed to get element rect: %s - %s", locator, e.getMessage());
            return Collections.emptyMap();
        }
    }
    
    @AutoDef
    public MobileCustom swipeGesture(String direction) {
        HashMap<String, Object> scrollObject = new HashMap<>();
        scrollObject.put("direction", direction);
        scrollObject.put(SPEED, Configuration.getString("mobile.swipe.speed", "500"));
        script("mobile: scroll", scrollObject);
        return this;
    }
    
    @AutoDef
    public MobileCustom dragAndDrop(String locatorA, String locatorB) {
        Logger.debug("Drag and drop from %s to %s", locatorA, locatorB);
        
        if (!isDisplayed(locatorA)) {
            throw new ElementNotFoundException("Source element not found: " + locatorA);
        }
        if (!isDisplayed(locatorB)) {
            throw new ElementNotFoundException("Target element not found: " + locatorB);
        }
        
        Map<String, Object> elementIdA = getLocation(locatorA);
        Map<String, Object> elementIdB = getLocation(locatorB);
        
        swipeV2(
                elementIdA.get(X),
                elementIdA.get(Y),
                elementIdB.get(X),
                elementIdB.get(Y)
        );
        Logger.debug("Drag and drop completed");
        return this;
    }
    
    @AutoDef
    public MobileCustom tapByDimension(int x, int y) {
        Logger.debug("Tapping at coordinates: (%d, %d)", x, y);
        
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be positive");
        }
        
        int screenWidth = windowWidth();
        int screenHeight = windowHeight();
        
        if (x > screenWidth || y > screenHeight) {
            Logger.warn("Coordinates (%d, %d) outside screen bounds (%dx%d)", x, y, screenWidth, screenHeight);
        }
        
        Map<String, Object> body = new HashMap<>();
        body.put("x", x);
        body.put("y", y);
        script("mobile: tap", body);
        return this;
    }

    @AutoDef
    public MobileCustom pressKeyCode(Object keycode) {
        Map<String, Object> body = new HashMap<>();
        body.put("keycode", keycode);
        http.path(APPIUM, DEVICE, "press_keycode").post(body);
        return this;
    }


    @AutoDef
    public boolean waitForDisplayed(String locator) {
        return waitForDisplayed(TIMEOUT, RETRY_TIME, locator);
    }

    @AutoDef
    public boolean waitForDisplayed(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> isDisplayed(locator));
    }

    @AutoDef
    public boolean waitEnabled(String locator) {
        return waitForEnabled(TIMEOUT, RETRY_TIME, locator);
    }

    @AutoDef
    public boolean waitForEnabled(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> isEnabled(locator));
    }

    private MobileCustom actions(String actions) {
        http.path("actions").postJson(actions);
        return this;
    }


    
    /**
     * App state management
     */
    @AutoDef
    public MobileCustom backgroundApp(int seconds) {
        Logger.debug("Putting app in background for %d seconds", seconds);
        Map<String, Object> params = new HashMap<>();
        params.put("seconds", seconds);
        script("mobile: backgroundApp", params);
        return this;
    }

    @AutoDef
    public boolean isAndroid() {
        return this.getClass().getName().toLowerCase().contains("android");
    }

    @AutoDef
    public boolean isIOS() {
        return this.getClass().getName().toLowerCase().contains("ios");
    }

    @AutoDef
    public MobileCustom setNetworkConnection(boolean wifi, boolean data, boolean airplane) {
        Logger.debug("Setting network: wifi=%s, data=%s, airplane=%s", wifi, data, airplane);

        int connectionType = 0;
        if (airplane) connectionType += 1;
        if (wifi) connectionType += 2;
        if (data) connectionType += 4;

        Map<String, Object> params = new HashMap<>();
        params.put("type", connectionType);
        http.path(APPIUM, "network_connection").post(params);
        return this;
    }

    @AutoDef
    public void switchToDriver(String sessionName) {
        Driver driver = DriverManager.getDriver(sessionName);
        if (driver == null) {
            throw new IllegalArgumentException("Driver session not found: " + sessionName);
        }
        ScenarioEngine.get().setDriver(driver);
        Logger.debug("Switched to driver session: %s", sessionName);
    }

    @AutoDef
    public abstract DriverType getDriverType();
    @AutoDef
    public abstract MobileCustom simulateFingerprint(boolean match);

    @AutoDef
    public String getPageSource() {
        return http.path("source").get().json().get(VALUE_KEY);
    }

    @AutoDef
    public List<Map<String, Object>> getAllElementsInfo() {
        String xml = getPageSource();
        List<Map<String, Object>> elements = new ArrayList<>();
        String[] lines = xml.split("\n");
        for (String line : lines){
            if(line.contains("class=\"") && (
                    line.contains("text=\"") ||
                    line.contains("resource-id=\"") ||
                    line.contains("content-desc=\"") ||
                    line.contains("clickable=\"true\"")
            )){
                Map<String, Object> element = new HashMap<>();
                element.put("text", extractAttr(line, "text"));
                element.put("resource-id", extractAttr(line, "resource-id"));
                element.put("content-desc", extractAttr(line, "content-desc"));
                element.put("clickable", extractAttr(line, "clickable"));
                element.put("enabled", extractAttr(line, "enabled"));
                element.put("bounds", extractAttr(line, "bounds"));

                List<String> locators = new ArrayList<>();
                String rid = (String) element.get("resource-id");
                String txt = (String) element.get("text");
                String desc = (String) element.get("content-desc");

                if(rid != null && !rid.isEmpty()){
                    locators.add("//*[@resource-id='" + rid + "']");
                }
                if(txt != null && !txt.isEmpty()){
                    locators.add("//*[@text='" + txt + "']");
                }
                if(desc != null && !desc.isEmpty()){
                    locators.add("//*[@content-desc='" + desc + "']");
                }

                element.put("locators", locators);
                if(!locators.isEmpty()) elements.add(element);
            }
        }
        return elements;
    }

    private String extractAttr(String line, String attr){
        String pattern = attr + "=\"([^\"]*)\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(line);
        return m.find() ? m.group(1) : null;
    }
}
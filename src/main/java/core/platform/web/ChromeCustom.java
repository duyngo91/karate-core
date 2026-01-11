package core.platform.web;

import com.intuit.karate.FileUtils;
import com.intuit.karate.Http;
import com.intuit.karate.core.*;
import com.intuit.karate.driver.DevToolsDriver;
import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.DriverOptions;
import com.intuit.karate.driver.Element;
import com.intuit.karate.http.HttpClientFactory;
import com.intuit.karate.http.Response;
import com.intuit.karate.shell.Command;
import core.healing.IHealingDriver;
import core.healing.HealingInitializer;
import core.healing.LocatorMapper;
import core.healing.SelfHealingDriver;
import core.platform.common.Configuration;
import core.platform.common.Constants;
import core.platform.exceptions.ElementNotFoundException;
import core.platform.manager.DriverManager;
import core.platform.utils.Logger;
import core.platform.web.element.CheckBox;
import core.platform.web.element.ShadowElement;
import core.platform.web.element.Tab;
import core.platform.web.element.WebElement;
import core.platform.web.element.droplist.DropListServiceManager;
import core.platform.web.element.table.TableServiceManager;
import core.platform.web.exceptions.RunException;
import core.platform.web.network.MessageHandler;
import core.platform.web.wait.Wait;
import net.minidev.json.JSONArray;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("java:S1068")
public class ChromeCustom extends DevToolsDriver implements IHealingDriver {
    private String currentDropListType = "defaultDropList";
    private String currentTableType = "defaultTable";
    public final DropListServiceManager dlManager;
    public final TableServiceManager tableManager;
    public static final String DRIVER_TYPE = "ChromeCustom";
    private static final int MIN_POINT = Constants.MIN_SCROLL_POINT;
    private static final int MAX_POINT = Constants.MAX_SCROLL_POINT;
    private final MessageHandler messageHandler;
    private final SelfHealingDriver healer;

    public static String of(String path) {
        if (path.contains("/"))
            return Arrays.stream(path.split("/")).collect(Collectors.joining(Character.toString(File.separatorChar)));
        if (path.contains("\\"))
            return Arrays.stream(path.split("\\\\"))
                    .collect(Collectors.joining(Character.toString(File.separatorChar)));
        return path;
    }

    public static String getKaratePath(String path) {
        String javaPath = System.getProperty("user.dir")
                + File.separatorChar + "src"
                + File.separatorChar + "test"
                + File.separatorChar + "java";

        if (path.contains("classpath:"))
            return of(path.replace("classpath:", javaPath + File.separatorChar));
        if (path.contains("file:"))
            return of(path.replace("file:", javaPath + File.separatorChar));
        return path;
    }

    protected ChromeCustom(DriverOptions options, Command command, String webSocketUrl) {
        super(options, command, webSocketUrl);
        messageHandler = new MessageHandler(options, this);
        super.client.setTextHandler(messageHandler.createTextHandler());
        this.dlManager = DropListServiceManager.getInstance();
        this.tableManager = TableServiceManager.getInstance();

        // Auto-initialize healing if enabled
        HealingInitializer.autoInit();
        this.healer = new SelfHealingDriver(this);
    }

    private static String determineChromePath() {
        if (FileUtils.isOsWindows()) {
            return com.intuit.karate.driver.chrome.Chrome.DEFAULT_PATH_WIN;
        } else if (FileUtils.isOsMacOsX()) {
            return com.intuit.karate.driver.chrome.Chrome.DEFAULT_PATH_MAC;
        } else {
            return com.intuit.karate.driver.chrome.Chrome.DEFAULT_PATH_LINUX;
        }
    }

    private static DriverOptions getDriverOptions(Map<String, Object> map, ScenarioRuntime sr) {
        DriverOptions options = new DriverOptions(map, sr, 9222, determineChromePath());
        options.arg("--remote-debugging-port=" + options.port);
        options.arg("--remote-allow-origins=*");
        options.arg("--no-first-run");
        if (options.userDataDir != null) {
            options.arg("--user-data-dir=" + options.userDataDir);
        }
        options.arg("--disable-popup-blocking");
        if (options.headless) {
            options.arg("--headless");
        }
        return options;
    }

    private static String startWebSocket(DriverOptions options, Response res) {
        String webSocketUrl = null;
        List<Map<String, Object>> targets = res.json().asList();
        for (Map<String, Object> target : targets) {
            String targetUrl = (String) target.get("url");
            String targetType = (String) target.get("type");

            // Skip invalid entries using inverted conditions
            if (targetUrl != null && !targetUrl.startsWith("chrome-") && "page".equals(targetType)) {

                webSocketUrl = (String) target.get("webSocketDebuggerUrl");

                // Break if we found either the first valid entry or matching URL
                if (options.attach == null || targetUrl.contains(options.attach)) {
                    break;
                }
            }
        }
        return webSocketUrl;
    }

    private static void validateResponse(Command command, Response res, Http http) {
        if (res.json().asList().isEmpty()) {
            if (command != null) {
                command.close(true);
            }
            throw new RunException("chrome server returned empty list from " + http.urlBase);
        }
    }

    private static ChromeCustom initChromeCustom(DriverOptions options, Command command, String webSocketUrl) {
        ChromeCustom chrome = new ChromeCustom(options, command, webSocketUrl);
        chrome.activate();
        chrome.enablePageEvents();
        chrome.enableRuntimeEvents();

        if (!options.headless) {
            chrome.initWindowIdAndState();
        }
        return chrome;
    }

    public static ChromeCustom start(Map<String, Object> options) {
        if (options == null) {
            options = new HashMap();
        }
        options.putIfAbsent("type", DRIVER_TYPE);
        ScenarioRuntime runtime = FeatureRuntime.forTempUse(HttpClientFactory.DEFAULT).scenarios.next();
        ScenarioEngine.set(runtime.engine);
        return start(options, runtime);
    }

    public static ChromeCustom start() {
        return start(null);
    }

    public static ChromeCustom start(Map<String, Object> map, ScenarioRuntime sr) {
        return start(map, sr, "chrome_" + Thread.currentThread().getId());
    }

    public static ChromeCustom start(Map<String, Object> map, ScenarioRuntime sr, String sessionName) {
        DriverOptions options = getDriverOptions(map, sr);
        Command command = options.startProcess();
        Http http = options.getHttp();
        Command.waitForHttp(http.urlBase + "/json", r -> r.getStatus() == 200 && !r.json().asList().isEmpty());
        Response res = http.path("json").get();
        validateResponse(command, res, http);
        String webSocketUrl = startWebSocket(options, res);
        ChromeCustom chromeCustom = initChromeCustom(options, command, webSocketUrl);
        DriverManager.addDriver(sessionName, chromeCustom);
        return chromeCustom;
    }

    @Override
    public List<String> methodNames() {
        List<String> methods = super.methodNames();
        methods.addAll(Plugin.methodNames(ChromeCustom.class));
        return methods;
    }

    @AutoDef
    public void inputValue(String xpath, Object value) {
        String func = "(function() {\n" +
                "var e = %s;\n" +
                "var event = new Event('input', {\n" +
                "'bubbles': true,\n" +
                "'cancelable': true\n" +
                "});\n" +
                "e.value = '${value}';\n" +
                "e.dispatchEvent(event);\n" +
                "})();";
        String temp = String.format(func.replace("${value}", value.toString()), xpath);
        script(temp);
    }

    /*
     * Simulate a natural mouse-click
     */
    @AutoDef
    public void mouseClick(String xpath) {
        String func = "(function() {\n" +
                "var e = %s;" +
                "    var mouseover = new Event(\"mouseover\", { bubbles: true, cancelable: true });\n" +
                "    var mousedown = new Event(\"mousedown\", { bubbles: true, cancelable: true });\n" +
                "    var mouseup = new Event(\"mouseup\", { bubbles: true, cancelable: true });\n" +
                "    var click = new Event(\"click\", { bubbles: true, cancelable: true });\n" +
                "    e.dispatchEvent(mouseover);\n" +
                "    e.dispatchEvent(mousedown);\n" +
                "    e.dispatchEvent(mouseup);\n" +
                "    e.dispatchEvent(click);\n" +
                "})();";
        script(String.format(func, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public void setDownloadPath(String path) {
        Map<String, Object> params = new HashMap<>();
        params.put("behavior", "allow");
        params.put("downloadPath", getKaratePath(path));
        Map<String, Object> body = new HashMap<>();

        body.put("method", "Browser.setDownloadBehavior");
        body.put("params", params);

        send(body);
    }

    @AutoDef
    public void setDownloadPath() {
        setDownloadPath(Configuration.getString("download.path", Constants.DEFAULT_DOWNLOAD_PATH));
    }

    @AutoDef
    public List<String> getAllText(String locator) {
        JSONArray array = null;
        try {
            array = (JSONArray) scriptAll(locator, "_.innerText");
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
        return array == null ? new ArrayList<>()
                : array.stream().map(o -> o.toString().trim()).collect(Collectors.toList());
    }

    @AutoDef
    public List<Boolean> getAllDisabled(String locator) {
        JSONArray array = null;
        try {
            array = (JSONArray) scriptAll(locator, "_.disabled");
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
        return array == null ? new ArrayList<>()
                : array.stream().map(o -> Boolean.parseBoolean(o.toString())).collect(Collectors.toList());
    }

    @AutoDef
    public int count(String locator) {
        return new WebElement(this, locator).count();
    }

    @AutoDef
    public boolean exist(String locator) {
        return count(locator) > 0;
    }

    @AutoDef
    public boolean waitAttributeHasValue(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator,
            String attribute) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> {
            String value = attribute(locator, attribute);
            return value != null && !value.isEmpty();
        });
    }

    @AutoDef
    public boolean waitPropertyHasValue(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator,
            String attribute) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> {
            String value = property(locator, attribute);
            return value != null && !value.isEmpty();
        });
    }

    @AutoDef
    public boolean waitExist(String locator) {
        return waitExist(Configuration.getShortTimeout(), Constants.NO_RETRY, locator);
    }

    @AutoDef
    public boolean waitExist(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> exist(locator));
    }

    @AutoDef
    public boolean waitNotExist(String locator) {
        return waitNotExist(Configuration.getShortTimeout(), Constants.NO_RETRY, locator);
    }

    @AutoDef
    public boolean waitNotExist(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> !exist(locator));
    }

    @AutoDef
    public boolean waitTextIs(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator, String expected) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds,
                () -> !text(locator).isEmpty() && text(locator).trim().equalsIgnoreCase(expected));
    }

    @AutoDef
    public boolean waitExistText(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> !text(locator).isEmpty());
    }

    @AutoDef
    public boolean waitTextMatchRegex(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator,
            String regex) {
        return Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds,
                () -> !extract(getText(locator).trim(), regex).trim().isEmpty());
    }

    @AutoDef
    public String waitAndGetText(String locator) {
        return waitAndGetText(Configuration.getDefaultTimeout(), Constants.NO_RETRY, locator);
    }

    @AutoDef
    public String waitAndGetText(int timeOutMilliSeconds, int retryTimeMilliSeconds, String locator) {
        Logger.debug("Waiting for text in element: %s (timeout: %dms)", locator, timeOutMilliSeconds);

        AtomicReference<String> text = new AtomicReference<>("");
        boolean success = Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> {
            try {
                String result = text(locator);
                if (result != null && !result.isEmpty()) {
                    text.set(result);
                    return true;
                }
            } catch (Exception e) {
                Logger.debug("Exception getting text from %s: %s", locator, e.getMessage());
            }
            return false;
        });

        if (!success) {
            Logger.warn("Timeout waiting for text in element: %s", locator);
            throw new ElementNotFoundException(locator);
        }

        String result = text.get() == null ? null : text.get().trim();
        Logger.debug("Retrieved text: '%s' from element: %s", result, locator);
        return result;
    }

    @AutoDef
    public String getValue(String xpath) {
        String func = "(function() {\n" +
                "var e = %s;\n" +
                "return e.value})();";
        String temp = String.format(func, DriverOptions.selector(xpath));
        return String.valueOf(script(temp));
    }

    @AutoDef
    public void remove(String xpath) {
        String func = "(function() {\n" +
                "var e = %s;\n" +
                "e.remove()})();";
        String temp = String.format(func, DriverOptions.selector(xpath));
        script(temp);
    }

    @AutoDef
    public byte[] readFileAsBytes(String path) {
        return FileUtils.toBytes(new File(path));
    }

    @AutoDef
    public String getText(String xpath) {
        String value = (String) script(xpath, "_.textContent");
        return value.trim();
    }

    @AutoDef
    public String getCssValue(String xpath, String cssAttribute) {
        String value = (String) script(xpath, String.format("_ => getComputedStyle(_)['%s']", cssAttribute));
        return value.trim();
    }

    @AutoDef
    public String getValueFromProperties(String locator, String attribute) {
        return property(locator, attribute);
    }

    @AutoDef
    public String outerText(String xpath) {
        return script(xpath, "_.outerText").toString().trim();
    }

    @AutoDef
    public String innerText(String xpath) {
        return script(xpath, "_.innerText").toString().trim();
    }

    @AutoDef
    public Object checked(String xpath) {
        return script(xpath, "_.checked");
    }

    @AutoDef
    public Object selected(String xpath) {
        return script(xpath, "_.selectedIndex");
    }

    @AutoDef
    public Object getSelectedText(String xpath) {
        int index = (int) selected(xpath);
        return (String) script(xpath, "_.options[" + index + "].text");
    }

    private static String extract(String content, String regex) {
        return extract(content, regex, 1);
    }

    private static String extract(String content, String regex, int groupIndex) {
        if (content != null && !content.isEmpty()) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(groupIndex);
            }
        }
        return null;
    }

    public void setCheckBox(String locator, boolean expected) {
        waitFor(locator);
        if (!checked(locator).equals(expected))
            click(locator);
    }

    @Override
    public Element waitFor(String locator) {
        // 1. Check Global Cache first
        if (core.healing.HealingCache.getInstance().contains(locator)) {
            String cached = core.healing.HealingCache.getInstance().get(locator);
            Logger.info("[Cache Hit] waitFor: %s -> %s", locator, cached);
            try {
                return new WebElement(this, getOptions().waitForAny(this, cached));
            } catch (Exception e) {
                Logger.warn("[Cache Invalid] Cached locator failed: %s", cached);
                // If cached locator fails, we can either remove it or fall back
            }
        }

        try {
            Element el = new WebElement(this, getOptions().waitForAny(this, locator));
            recordSuccess(locator);
            return el;
        } catch (Exception e) {
            String healed = tryHeal(locator);
            return new WebElement(this, getOptions().waitForAny(this, healed, locator));
        }
    }

    @Override
    public Element focus(String locator) {
        String healed = tryHeal(locator);
        return new WebElement(this, super.focus(healed));
    }

    @Override
    public Element clear(String locator) {
        String healed = tryHeal(locator);
        return new WebElement(this, super.clear(healed));
    }

    @Override
    public Element click(String locator) {
        String healed = tryHeal(locator);
        return new WebElement(this, super.click(healed));
    }

    public Element select(String locator, String text) {
        String healed = tryHeal(locator);
        return new WebElement(this, super.select(healed, text));
    }

    @Override
    public Element select(String locator, int index) {
        String healed = tryHeal(locator);
        return new WebElement(this, super.select(healed, index));
    }

    @Override
    public Element input(String locator, String value) {
        String healed = tryHeal(locator);
        return new WebElement(this, super.input(healed, value));
    }

    private String tryHeal(String locator) {
        try {
            // 1. Check Global Cache first
            if (core.healing.HealingConfig.getInstance().isCacheEnabled() && 
                core.healing.HealingCache.getInstance().contains(locator)) {
                String cached = core.healing.HealingCache.getInstance().get(locator);
                Logger.info("[Cache] Using cached locator: %s -> %s", locator, cached);
                return cached;
            }

            LocatorMapper mapper = LocatorMapper.getInstance();
            if (mapper.isManaged(locator)) {
                String elementId = mapper.getElementId(locator);
                Logger.info("[Healing] Managed locator failing: %s (ID: %s). Attempting heal...", locator, elementId);
                String healed = this.healer.findElement(elementId, locator);
                if (healed != null && !healed.equals(locator)) {
                    Logger.info("[Healing] Successfully healed to: %s", healed);
                    return healed;
                }
                Logger.warn("[Healing] Could not find a better alternative for: %s", locator);
            } else {
                Logger.debug("[Healing] Locator not managed, skipping: %s", locator);
            }
        } catch (Exception e) {
            Logger.debug("Healing skipped: %s", e.getMessage());
        }
        return locator;
    }

    private void recordSuccess(String locator) {
        try {
            if (core.healing.HealingConfig.getInstance().isCaptureGoldenState()) {
                LocatorMapper mapper = LocatorMapper.getInstance();
                if (mapper.isManaged(locator)) {
                    String elementId = mapper.getElementId(locator);
                    core.healing.rag.GoldenStateRecorder.getInstance().captureAndSave(this, elementId, locator);
                }
            }
        } catch (Exception e) {
            Logger.warn("Failed to record success for %s: %s", locator, e.getMessage());
        }
    }

    @AutoDef
    public String innerHTML(String xpath) {
        return new WebElement(this, xpath).innerHTML();
    }

    @AutoDef
    public String outerHTML(String xpath) {
        return new WebElement(this, xpath).outerHTML();
    }

    @AutoDef
    public void clickAll(String locator) {
        new WebElement(this, locator).clickAll();
    }

    @AutoDef
    public Tab tab() {
        return new Tab(this);
    }

    @AutoDef
    public Object checkbox(String xpath) {
        retryIfEnabled(xpath);
        return new CheckBox(this, xpath);
    }

    @AutoDef
    public Object table(String xpathHeader) {
        retryIfEnabled(xpathHeader);
        return tableManager.getService(currentTableType).createTable(this, xpathHeader);
    }

    @AutoDef
    public Object table(String xpathHeader, String xpathRow) {
        retryIfEnabled(xpathHeader);
        return tableManager.getService(currentTableType).createTable(this, xpathHeader, xpathRow);
    }

    @AutoDef
    public Element droplist(String dropList) {
        retryIfEnabled(dropList);
        return dlManager.getService(currentDropListType).createDropList(this, dropList);
    }

    @AutoDef
    public Element droplist(String dropList, String items) {
        retryIfEnabled(dropList);
        return dlManager.getService(currentDropListType).createDropList(this, dropList, items);
    }

    @AutoDef
    public Element droplist(String dropList, String searchField, String items) {
        retryIfEnabled(dropList);
        return dlManager.getService(currentDropListType).createDropList(this, dropList, searchField, items);
    }

    public ChromeCustom useDropListType(String type) {
        if (!dlManager.hasService(type)) {
            throw new IllegalArgumentException("DropList type not supported: " + type);
        }
        this.currentDropListType = type;
        return this;
    }

    /**
     * Lấy dữ liệu response API của website dựa trên URL được chỉ định.
     * <p>
     * Ví dụ sử dụng:
     * 
     * <pre>
     * {@code
     * // Lấy response cho API token
     * * def api = getResponseAPI("token")
     * * print api[0].params.response.body
     */
    @AutoDef
    public Object getResponseAPI(String url) {
        return messageHandler.getResponseAPI(url);
    }

    @AutoDef
    public Object getRequestAPI(String url) {
        return messageHandler.getRequestAPI(url);
    }

    /**
     * Di chuyển màn hình đến phần tử được chỉ định bằng XPath.
     * Phần tử sẽ được căn giữa màn hình với hiệu ứng cuộn mượt.
     *
     * @param xpath Biểu thức XPath để xác định vị trí phần tử cần cuộn đến
     * @throws RuntimeException         khi việc thực thi JavaScript thất bại
     * @throws IllegalArgumentException khi biểu thức XPath không hợp lệ
     *                                  <p>
     *                                  Ví dụ sử dụng:
     * 
     *                                  <pre>
     *                                                                                                    {@code
     *                                                                                                    // Cuộn đến phần tử div có class là 'target-element'
     *                                                                                                    scrollIntoDiv("//div[@class='target-element']");
     *
     *                                                                                                    // Cuộn đến phần tử td thứ 5 trong hàng thứ 3 của bảng
     *                                                                                                    scrollIntoDiv("//table//tr[3]//td[5]");
     *                                                                                                    }
     *                                                                                                    </pre>
     *                                  <p>
     *                                  Chú ý:
     *                                  - Đảm bảo phần tử tồn tại trong DOM trước
     *                                  khi gọi hàm
     *                                  - Hiệu ứng cuộn mượt (smooth scroll) được hỗ
     *                                  trợ trên hầu hết các trình duyệt hiện đại
     */
    @AutoDef
    public void scrollIntoView(String xpath) {
        String script = "(function() {\n" +
                "    var element = %s;\n" +
                "    if (element) {\n" +
                "        element.scrollIntoView({behavior: 'smooth', block: 'center'});\n" +
                "    }\n" +
                "    return true;\n" +
                "})();";
        script(String.format(script, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public String textContent(String xpath) {
        return new WebElement(this, xpath).textContent();
    }

    @AutoDef
    public Object adobeDataLayer() {
        return script("(function() { return adobeDataLayer;})();");
    }

    @AutoDef
    public void scrollToBottom() {
        script("window.scrollTo(0, document.body.scrollHeight);");
    }

    @AutoDef
    public void scrollToTop() {
        script("window.scrollTo(0, 0);");
    }

    @AutoDef
    public void scrollToLeft() {
        script("window.scrollTo(0, window.pageYOffset);");
    }

    @AutoDef
    public void scrollToRight() {
        script("window.scrollTo(document.body.scrollWidth, window.pageYOffset);");
    }

    @AutoDef
    public void scrollElementBy(String xpath, int x, int y) {
        Logger.debug("Scrolling element %s by x:%d, y:%d", xpath, x, y);

        if (!exist(xpath)) {
            throw new ElementNotFoundException(xpath);
        }

        String func = "var e = %s; e.scrollBy(${x}, ${y});";
        script(
                String.format(
                        func.replace("${x}", String.valueOf(x))
                                .replace("${y}", String.valueOf(y)),
                        DriverOptions.selector(xpath)));
    }

    @AutoDef
    public int getOffsetHeight(String xpath) {
        String func = "var e = %s; e.offsetHeight;";
        return (int) script(
                String.format(func, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public int getOffsetWidth(String xpath) {
        String func = "var e = %s; e.offsetWidth;";
        return (int) script(
                String.format(func, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public int getScrollTop(String xpath) {
        String func = "var e = %s; e.scrollTop;";
        return (int) script(
                String.format(func, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public int getScrollLeft(String xpath) {
        String func = "var e = %s; e.scrollLeft;";
        return (int) script(
                String.format(func, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public int getScrollWidth(String xpath) {
        String func = "var e = %s; e.scrollWidth;";
        return (int) script(
                String.format(func, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public int getScrollHeight(String xpath) {
        String func = "var e = %s; e.scrollHeight;";
        return (int) script(
                String.format(func, DriverOptions.selector(xpath)));
    }

    @AutoDef
    public boolean isScrollToEndOfTop(String xpath) {
        return getScrollTop(xpath) <= 0;
    }

    @AutoDef
    public boolean isScrollToEndOfBottom(String xpath) {
        return getOffsetHeight(xpath) + getScrollTop(xpath) >= getScrollHeight(xpath);
    }

    @AutoDef
    public boolean isScrollToEndOfRight(String xpath) {
        return getOffsetWidth(xpath) + getScrollLeft(xpath) >= getScrollWidth(xpath);
    }

    @AutoDef
    public boolean isScrollToEndOfLeft(String xpath) {
        return getScrollLeft(xpath) <= 0;
    }

    @AutoDef
    public void scrollToBottomInfoElement(String xpath) {
        scrollElementBy(xpath, MAX_POINT, MIN_POINT);
    }

    @AutoDef
    public void scrollToLeftInfoElement(String xpath) {
        scrollElementBy(xpath, -MAX_POINT, MIN_POINT);
    }

    @AutoDef
    public void scrollToTopInfoElement(String xpath) {
        scrollElementBy(xpath, MIN_POINT, MAX_POINT);
    }

    @AutoDef
    public ShadowElement shadowElement(String hostSelector, String elementSelector) {
        return new ShadowElement(this, hostSelector, elementSelector);
    }

    public String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "txt":
                return "text/plain";
            case "csv":
                return "text/csv";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "zip":
                return "application/zip";
            case "mp4":
                return "video/mp4";
            case "mp3":
                return "audio/mpeg";
            default:
                return "application/octet-stream";
        }
    }

    public Map<String, Object> getRect(String xpath) {
        return locate(xpath).getPosition();
    }

    public double midX(String xpath) {
        Map<String, Object> rect = getRect(xpath);
        return Double.parseDouble(rect.get("x").toString()) + Double.parseDouble(rect.get("width").toString()) / 2;
    }

    public double midY(String xpath) {
        Map<String, Object> rect = getRect(xpath);
        return Double.parseDouble(rect.get("y").toString()) + Double.parseDouble(rect.get("height").toString()) / 2;
    }

    @AutoDef
    public void uploadFileByDragEvent(String xpath, String filePath) {
        int startX = 0;
        int startY = 0;
        String eCursor = "var cursor = document.getElementById('drag-cursor');";
        File file = new File(getKaratePath(filePath));
        String mimeType = getMimeType(filePath);
        double endX = midX(xpath);
        double endY = midY(xpath);

        // Create file drag visual
        script(
                "var cursor = document.createElement('div');" +
                        "cursor.id = 'drag-cursor';" +
                        "cursor.style.cssText = 'position:fixed;padding:8px;background:rgba(255,255,255,0.9);border:1px solid #ccc;border-radius:4px;box-shadow:0 2px 8px rgba(0,0,0,0.3);z-index:9999;pointer-events:none;font-family:Arial,sans-serif;font-size:12px;';"
                        +
                        "cursor.innerHTML = '📄 " + file.getName() + "';" +
                        "cursor.style.left = '" + startX + "px';" +
                        "cursor.style.top = '" + startY + "px';" +
                        "document.body.appendChild(cursor);");

        // Animate cursor movement
        int steps = 20;
        double deltaX = (endX - startX) / steps;
        double deltaY = (endY - startY) / steps;

        for (int i = 0; i <= steps; i++) {
            double currentX = startX + (deltaX * i);
            double currentY = startY + (deltaY * i);

            script(String.format(
                    "%s if (cursor) {" +
                            "  cursor.style.left = '%spx';" +
                            "  cursor.style.top = '%spx';" +
                            "  cursor.style.transform = 'rotate(' + (%d * 2) + 'deg)';" +
                            "}",
                    eCursor, currentX, currentY, i));

            delay(50);
        }

        // Highlight target on hover
        script(String.format(
                "var e = %s; e.style.border = '3px dashed #007cba';",
                DriverOptions.selector(xpath)));

        delay(300);

        // Drop file
        String dropScript = String.format(
                "(function() {" +
                        "  var el = %s;" +
                        "  var file = new File(['dummy'], '%s', {type: '%s'});" +
                        "  var dt = new DataTransfer();" +
                        "  dt.items.add(file);" +
                        "  el.dispatchEvent(new DragEvent('drop', {bubbles: true, dataTransfer: dt}));" +
                        "  if (el.files !== undefined) el.files = dt.files;" +
                        "  el.dispatchEvent(new Event('change', {bubbles: true}));" +
                        "  el.style.border = '2px solid #4caf50';" +
                        "  el.style.backgroundColor = '#e8f5e8';" +
                        "})();",
                DriverOptions.selector(xpath), file.getName(), mimeType);
        script(dropScript);

        // Fade out and remove cursor
        script(String.format("%s if (cursor) cursor.style.opacity = '0.5';", eCursor));
        delay(300);
        script(String.format("%s if (cursor) cursor.remove();", eCursor));
    }

    @AutoDef
    public void uploadFile(String inputXpath, String filePath) {
        Logger.info("Uploading file %s to element %s", filePath, inputXpath);

        if (!exist(inputXpath)) {
            throw new ElementNotFoundException(inputXpath);
        }

        File file = new File(getKaratePath(filePath));
        if (!file.exists()) {
            throw new RuntimeException("File not found: " + filePath);
        }

        String mimeType = getMimeType(filePath);

        byte[] content = FileUtils.toBytes(file);
        String base64 = Base64.getEncoder().encodeToString(content);

        String uploadScript = String.format(
                "(function() {" +
                        "  var input = %s;" +
                        "  var b64 = '%s';" +
                        "  var bin = atob(b64);" +
                        "  var arr = new Uint8Array(bin.length);" +
                        "  for (var i = 0; i < bin.length; i++) arr[i] = bin.charCodeAt(i);" +
                        "  var file = new File([arr], '%s', {type: '%s'});" +
                        "  var dt = new DataTransfer();" +
                        "  dt.items.add(file);" +
                        "  input.files = dt.files;" +
                        "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                        "})();",
                DriverOptions.selector(inputXpath), base64, file.getName(), mimeType);

        script(uploadScript);
        Logger.debug("File upload completed for: %s", filePath);
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
    public String getPageSource() {
        return (String) script("document.documentElement.outerHTML");
    }

    @AutoDef
    public String getOptimizedPageSource() {
        try {
            String jsScript = FileUtils.toString(getClass().getResourceAsStream("/get-optimized-page-source.js"));
            return script(jsScript).toString();
        } catch (Exception e) {
            Logger.error("Failed to load optimized page source script: %s", e.getMessage());
            return "{}";
        }
    }

    @Override
    public String getType() {
        return "WEB";
    }
}

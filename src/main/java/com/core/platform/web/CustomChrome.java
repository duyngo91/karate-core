package com.core.platform.web;

import com.intuit.karate.FileUtils;
import com.intuit.karate.Http;
import com.intuit.karate.core.AutoDef;
import com.intuit.karate.core.Plugin;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.driver.DevToolsDriver;
import com.intuit.karate.driver.DriverOptions;
import com.intuit.karate.http.Response;
import com.intuit.karate.shell.Command;
import com.core.platform.web.exception.RunException;
import com.core.platform.web.network.MessageHandler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intuit.karate.driver.chrome.Chrome.*;

public class CustomChrome extends DevToolsDriver {
    public static final String DRIVER_TYPE = "CustomChrome";
    private static final String XPATH = "${xpath}";
    public final MessageHandler messageHandler;

    protected CustomChrome(DriverOptions options, Command command, String webSocketUrl) {
        super(options, command, webSocketUrl);
        messageHandler = new MessageHandler(options, this);
        super.client.setTextHandler(messageHandler.createTextHandler());
    }

    public static CustomChrome start(Map<String, Object> map, ScenarioRuntime sr) {
        DriverOptions options = getDriverOptions(map, sr);
        Command command = options.startProcess();
        Http http = options.getHttp();
        Command.waitForHttp(http.urlBase + "/json", r -> r.getStatus() == 200 && !r.json().asList().isEmpty());
        Response res = http.path("json").get();
        validateResponse(command, res, http);
        return init(options, command, startWebSocket(options, res));
    }

    private static String startWebSocket(DriverOptions options, Response res) {
        String webSocketUrl = null;
        List<Map<String, Object>> targets = res.json().asList();
        for (Map<String, Object> target : targets) {
            String targetUrl = (String) target.get("url");
            String targetType = (String) target.get("type");

            if (targetUrl != null && !targetUrl.startsWith("chrome-") && "page".equals(targetType)) {
                webSocketUrl = (String) target.get("webSocketDebuggerUrl");

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

    private static String determineChromePath() {
        return FileUtils.isOsWindows() ? DEFAULT_PATH_WIN : FileUtils.isOsMacOsX() ? DEFAULT_PATH_MAC : DEFAULT_PATH_LINUX;
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
            options.arg("--headless=new");
        }
        return options;
    }

    private static CustomChrome init(DriverOptions options, Command command, String webSocketUrl) {
        CustomChrome chrome = new CustomChrome(options, command, webSocketUrl);
        chrome.activate();
        chrome.enablePageEvents();
        chrome.enableRuntimeEvents();

        if (!options.headless) {
            chrome.initWindowIdAndState();
        }
        return chrome;
    }

    private static String of(String path){
        if(path.contains("/"))
            return Arrays.stream(path.split("/"))
                    .collect(Collectors.joining(Character.toString(File.separatorChar)));
        if(path.contains("\\"))
            return Arrays.stream(path.split("\\\\"))
                    .collect(Collectors.joining(Character.toString(File.separatorChar)));
        return path;
    }

    private static String getKaratePath(String path){
        String javaPath = System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "test"
                + File.separator + "java";
        if(path.contains("classpath:")) return of(path.replace("classpath:", javaPath + File.separatorChar));
        if(path.contains("file:")) return of(path.replace("file:", javaPath + File.separatorChar));
        return path;
    }

    @Override
    public List<String> methodNames(){
        List<String> methods = super.methodNames();
        methods.addAll(Plugin.methodNames(CustomChrome.class));
        return methods;
    }

    @AutoDef
    public Object getResponseAPI(String url){ return messageHandler.getResponseXHR(url);}
    @AutoDef
    public Object getRequestAPI(String url){ return messageHandler.getRequestXHR(url);}
    @AutoDef
    public Object getResponse(){ return messageHandler.getResponsesAsJson(); }
    @AutoDef
    public Object getRequest(){ return messageHandler.getRequestsAsJson(); }



}

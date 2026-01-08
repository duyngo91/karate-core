package core.platform.mobile;

import com.intuit.karate.FileUtils;
import com.intuit.karate.core.AutoDef;
import com.intuit.karate.core.Plugin;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.driver.appium.MobileDriverOptions;
import core.platform.common.DriverType;
import core.platform.manager.DriverManager;
import core.platform.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidCustom extends MobileCustom {
    public static final String DRIVER_TYPE = "AndroidCustom";
    
    protected AndroidCustom(MobileDriverOptions options) {
        super(options);
    }

    public static AndroidCustom start(Map<String, Object> map, ScenarioRuntime sr) {
        return start(map, sr, "android_" + Thread.currentThread().getId());
    }
    
    public static AndroidCustom start(Map<String, Object> map, ScenarioRuntime sr, String sessionName) {
        MobileDriverOptions options = new MobileDriverOptions(map, sr, 4723, FileUtils.isOsWindows() ? "cmd.exe" : "appium");
        if (FileUtils.isOsWindows()) {
            options.arg("/C");
            options.arg("cmd.exe");
            options.arg("/K");
            options.arg("appium");
        }
        options.arg("--port=" + options.port);
        AndroidCustom androidCustom = new AndroidCustom(options);
        DriverManager.addDriver(sessionName, androidCustom);
        return androidCustom;
    }
    
    @Override
    public List<String> methodNames() {
        List<String> methods = super.methodNames();
        methods.addAll(Plugin.methodNames(AndroidCustom.class));
        return methods;
    }
    
    @AutoDef
    public AndroidCustom openNotification() {
        http.path("appium", "device", "open_notifications").post(null);
        return this;
    }
    
    @AutoDef
    public AndroidCustom fingerPrint(String fingerprintId) {
        Map<String, Object> body = new HashMap<>();
        body.put("fingerprintId", fingerprintId);
        http.path("appium", "app", "finger_print").post(body);
        return this;
    }
    
    @AutoDef
    public AndroidCustom adbShell(String command) {
        Map<String, Object> body = new HashMap<>();
        body.put("command", command);
        script("mobile: shell", body);
        return this;
    }
    
    @AutoDef
    public Map<String, Object> getNotifications() {
        return (Map<String, Object>) script("mobile: listSms", new HashMap<>());
    }

    @AutoDef
    public DriverType getDriverType() {
        return DriverType.ANDROID;
    }

    @AutoDef
    public MobileCustom simulateFingerprint(boolean match) {
        Logger.debug("Simulating fingerprint: match=%s", match);
        Map<String, Object> params = new HashMap<>();
        params.put("fingerprintId", match ? 1 : 2);
        script("mobile: fingerPrint", params);
        return this;
    }



}
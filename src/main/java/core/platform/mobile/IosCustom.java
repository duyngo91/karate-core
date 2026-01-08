package core.platform.mobile;

import com.intuit.karate.core.AutoDef;
import com.intuit.karate.core.Plugin;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.driver.Element;
import com.intuit.karate.driver.appium.MobileDriverOptions;
import core.platform.common.DriverType;
import core.platform.manager.DriverManager;
import core.platform.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IosCustom extends MobileCustom {
    public static final String DRIVER_TYPE = "IosCustom";
    
    protected IosCustom(MobileDriverOptions options) {
        super(options);
    }
    
    public static IosCustom start(Map<String, Object> map, ScenarioRuntime sr) {
        return start(map, sr, "ios_" + Thread.currentThread().getId());
    }
    
    public static IosCustom start(Map<String, Object> map, ScenarioRuntime sr, String sessionName) {
        MobileDriverOptions options = new MobileDriverOptions(map, sr, 4723, "appium");
        options.arg("--port=" + options.port);

        IosCustom iosCustom = new IosCustom(options);
        DriverManager.addDriver(sessionName, iosCustom);
        return iosCustom;
    }
    
    @Override
    public List<String> methodNames() {
        List<String> methods = super.methodNames();
        methods.addAll(Plugin.methodNames(IosCustom.class));
        return methods;
    }
    
    @AutoDef
    public IosCustom openNotification() {
        swipeV2(1, 1, 1, windowHeight());
        return this;
    }
    
    @AutoDef
    public IosCustom shake() {
        http.path("appium", "device", "shake").post(null);
        return this;
    }
    
    @Override
    @AutoDef
    public Element scrollDownToView(String locator) {
        int times = 0;
        do {
            boolean displayed = isDisplayed(locator);
            if (displayed) break;
            times++;
            swipeGesture("down");
        } while (times < 10);
        return E(locator);
    }
    
    @AutoDef
    public Map<String, Object> getNotifications() {
        // iOS doesn't support notification access via Appium
        return new HashMap<>();
    }

    @AutoDef
    public DriverType getDriverType() {
        return DriverType.IOS;
    }

    @AutoDef
    public MobileCustom simulateFingerprint(boolean match) {
        Logger.debug("Simulating fingerprint: match=%s", match);

        Map<String, Object> params = new HashMap<>();
        params.put("type", "touchId");
        params.put("match", match);
        script("mobile: sendBiometricMatch", params);
        return this;
    }





}
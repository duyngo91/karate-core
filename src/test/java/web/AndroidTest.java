package web;

import com.google.gson.Gson;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.http.HttpClientFactory;
import com.linecorp.armeria.internal.shaded.guava.reflect.TypeToken;
import core.platform.mobile.AndroidCustom;

import java.lang.reflect.Type;
import java.util.Map;

public class AndroidTest {
    public static void main(String[] args) {
        String capabilities =
                """
                {
                    "type" : "AndroidCustom",
                    "class" : "core.platform.mobile.AndroidCustom",
                    "webDriverUrl": "http://127.0.0.1:4723",
                    "showDriverLog": false,
                    "start": false,
                     "webDriverSession":{
                        "capabilities" :{
                            "alwaysMatch" : {
                                "platformName": "Android",
                                "appium:automationName": "UiAutomator2",
                                "appium:deviceName": "Android Emulator",
                                "appium:appPackage": "com.android.settings",
                                "appium:appActivity": "com.android.settings.Settings",
                                "appium:appWaitActivity": "com.android.settings.*",
                                "appium:skipServerInstallation" : false,
                                "appium:fullReset" : false,
                                "appium:noReset" :false,
                                "appium:uuid" : null,
                                "appium:autoGrantPermissions" : true,
                                "appium:ignoreHiddenApiPolicyError" : true,
                                "appium:disableWindowAnimation" : true,
                                "appium:newCommandTimeout" : 120000
                            }
                        }
                     }
                }
                """;
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = gson.fromJson(capabilities, type);
        FeatureRuntime featureRuntime = FeatureRuntime.forTempUse(HttpClientFactory.DEFAULT);
        ScenarioRuntime scenarioRuntime = featureRuntime.scenarios.next();
        AndroidCustom androidCustom = AndroidCustom.start(map, scenarioRuntime, "android_test");
        System.out.println("start " + capabilities);
        System.out.println("getAllElementsInfo " + androidCustom.getPageSource());
        System.out.println("getAllElementsInfo " + androidCustom.getAllElementsInfo());
    }
}

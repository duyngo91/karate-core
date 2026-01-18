package core.healing.application.port;

import com.intuit.karate.driver.Driver;

public interface IHealingDriver extends Driver {
     boolean exist(String locator);
     boolean exist(String locator, int timeOutMilliSeconds);
     String text(String locator);
     Object script(String js);
     String getType(); // e.g., "WEB", "ANDROID", "IOS"
}

package core.healing;

import com.intuit.karate.driver.Driver;

public interface IHealingDriver extends Driver {
    boolean exist(String locator);

    String text(String locator);

    Object script(String js);

    String getType(); // e.g., "WEB", "ANDROID", "IOS"
}

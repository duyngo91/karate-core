package core.healing;

import com.intuit.karate.driver.Driver;

import java.util.List;
import java.util.Map;

public interface IHealingDriver extends Driver {
    boolean exist(String locator);

    String text(String locator);

    Object script(String js);

    List<Map<String, Object>> visualTextMap();

    String getType(); // e.g., "WEB", "ANDROID", "IOS"
}

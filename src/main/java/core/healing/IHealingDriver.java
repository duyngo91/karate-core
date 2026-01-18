package core.healing;

import com.intuit.karate.driver.Driver;

public interface IHealingDriver extends Driver {
    public boolean exist(String locator);
    public boolean exist(String locator, int timeOutMilliSeconds);
    public String text(String locator);

    public Object script(String js);



    public String getType(); // e.g., "WEB", "ANDROID", "IOS"
}

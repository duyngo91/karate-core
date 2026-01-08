package core.platform.manager;

import com.intuit.karate.driver.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverManager {
    private static final ThreadLocal<Map<String, Driver>> drivers = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<List<Driver>> list = ThreadLocal.withInitial(ArrayList::new);

    public static void addDriver(String session, Driver driver) {
        drivers.get().put(session, driver);
        list.get().add(driver);
    }

    public static Driver getDriver(String session) {
        return drivers.get().get(session);
    }

    public static void clearAll() {
        list.get().forEach(driver -> {
            try {
                driver.quit();
            } catch (Exception e) {
                // Log error but continue cleanup
            }
        });
        drivers.get().clear();
        list.get().clear();
    }

    public static void removeDriver(String session) {
        Driver driver = drivers.get().remove(session);
        if (driver != null) {
            list.get().remove(driver);
            try {
                driver.quit();
            } catch (Exception e) {
                // Log error
            }
        }
    }

    public static int getDriverCount() {
        return list.get().size();
    }

    public static boolean hasDriver(String session) {
        return drivers.get().containsKey(session);
    }
}

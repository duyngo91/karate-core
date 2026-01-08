package core.platform.web.factory;

import com.intuit.karate.driver.DriverRunner;
import core.platform.mobile.AndroidCustom;
import core.platform.mobile.IosCustom;
import core.platform.web.ChromeCustom;

import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private DriverFactory() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Map<String, DriverRunner> getDrivers() {
        Map<String, DriverRunner> drivers = new HashMap<>();
        drivers.put(ChromeCustom.DRIVER_TYPE, ChromeCustom::start);
        drivers.put(AndroidCustom.DRIVER_TYPE, AndroidCustom::start);
        drivers.put(IosCustom.DRIVER_TYPE, IosCustom::start);
        return drivers;
    }

}

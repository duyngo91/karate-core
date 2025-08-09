package com.core.platform.web.factory;

import com.intuit.karate.driver.DriverRunner;
import com.core.platform.web.CustomChrome;

import java.util.HashMap;
import java.util.Map;

public class DriverFactory {
    private DriverFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Map<String, DriverRunner> getDrivers(){
        Map<String, DriverRunner> drivers = new HashMap<>();
        drivers.put(CustomChrome.DRIVER_TYPE, CustomChrome::start);
        return drivers;
    }
}

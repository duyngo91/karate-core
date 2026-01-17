package web;

import core.platform.manager.DriverManager;
import core.platform.web.ChromeCustom;

import java.util.HashMap;

public class WebTest {
    public static void main(String[] args) {
        ChromeCustom driver = ChromeCustom.start(new HashMap<>());
        driver.setUrl("https://globedr.com/");
        System.out.println(driver.getOptimizedPageSource());
        driver.delay(5000);
    }
}

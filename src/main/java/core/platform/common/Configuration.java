package core.platform.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "platform.properties";
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            // Use default values if config file not found
        }
    }
    
    public static int getTimeout(String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    public static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    // Specific getters
    public static int getDefaultTimeout() {
        return getTimeout("timeout.default", Constants.DEFAULT_TIMEOUT);
    }
    
    public static int getShortTimeout() {
        return getTimeout("timeout.short", Constants.SHORT_TIMEOUT);
    }
    
    public static int getLongTimeout() {
        return getTimeout("timeout.long", Constants.LONG_TIMEOUT);
    }
    
    public static int getDefaultRetry() {
        return getTimeout("retry.default", Constants.DEFAULT_RETRY);
    }
}
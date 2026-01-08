package core.platform.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public static void info(String message, Object... args) {
        log("INFO", message, args);
    }
    
    public static void debug(String message, Object... args) {
        log("DEBUG", message, args);
    }
    
    public static void warn(String message, Object... args) {
        log("WARN", message, args);
    }
    
    public static void error(String message, Object... args) {
        log("ERROR", message, args);
    }
    
    public static void error(String message, Throwable throwable, Object... args) {
        log("ERROR", message, args);
        System.err.println("Exception: " + throwable.getMessage());
    }
    
    private static void log(String level, String message, Object... args) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String formattedMessage = String.format(message, args);
        System.out.printf("[%s] %s - %s%n", timestamp, level, formattedMessage);
    }
}
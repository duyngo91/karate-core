package core.mcp.validation;

import java.util.Map;

public class ArgumentValidator {

    public static void requireNonNull(Map<String, Object> args, String... keys) {
        for (String key : keys) {
            if (!args.containsKey(key) || args.get(key) == null) {
                throw new IllegalArgumentException("Missing required argument: " + key);
            }
        }
    }

    public static void requireNonEmpty(Map<String, Object> args, String key) {
        requireNonNull(args, key);
        String value = args.get(key).toString();
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Argument cannot be empty: " + key);
        }
    }

    public static void validateUrl(String url) {
        if (!url.matches("^https?://.*")) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
    }

    public static void validateLocator(String locator) {
        if (locator == null || locator.trim().isEmpty()) {
            throw new IllegalArgumentException("Locator cannot be empty");
        }
    }

    public static void validateSession(String session) {
        if (session != null && !session.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid session name: " + session);
        }
    }
}

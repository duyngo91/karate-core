package core.healing;

import core.platform.utils.Logger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global in-memory cache for healed locators.
 * Stores mappings from [broken locator] -> [healed locator].
 * This cache is valid only for the current JVM session and allows
 * instant retrieval of previously healed elements without re-running the
 * healing logic.
 */
public class HealingCache {

    private static final HealingCache instance = new HealingCache();
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    private HealingCache() {
    }

    public static HealingCache getInstance() {
        return instance;
    }

    /**
     * Put a healed locator into the cache.
     * 
     * @param originalLocator The broken locator string used in the test.
     * @param healedLocator   The working locator found by the healing engine.
     */
    public void put(String originalLocator, String healedLocator) {
        if (!HealingConfig.getInstance().isCacheEnabled())
            return;
        if (originalLocator == null || healedLocator == null)
            return;
        if (originalLocator.equals(healedLocator))
            return;

        cache.put(originalLocator, healedLocator);
        Logger.debug("[HealingCache] Cached: '%s' -> '%s'", originalLocator, healedLocator);
    }

    /**
     * Retrieve a healed locator from the cache.
     * 
     * @param originalLocator The locator string to look up.
     * @return The healed locator if found, otherwise null.
     */
    public String get(String originalLocator) {
        return cache.get(originalLocator);
    }

    /**
     * Check if the cache contains a mapping for the given locator.
     */
    public boolean contains(String originalLocator) {
        return cache.containsKey(originalLocator);
    }

    /**
     * Clear the entire cache.
     */
    public void clear() {
        cache.clear();
        Logger.info("[HealingCache] Cache cleared.");
    }

    public int size() {
        return cache.size();
    }
}

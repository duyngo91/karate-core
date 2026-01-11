package core.healing;

import core.platform.utils.Logger;
import java.io.File;

public class HealingInitializer {
    private static boolean initialized = false;

    public static void autoInit() {
        if (initialized) {
            return;
        }

        HealingConfig config = HealingConfig.getInstance();

        if (config.isEnabled()) {
            Logger.info("Self-healing system enabled");
            String path = config.getLocatorPath();

            // Note: If path is a file (history), it might not be what loadLocatorsFromPath
            // expects (dir)
            // But we follow the user's requested logic for initialization
            loadLocatorsFromPath(path);

            // Register shutdown hook for monitoring report
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                HealingMonitor.getInstance().generateReport();
            }));

            initialized = true;
        }
    }

    private static void loadLocatorsFromPath(String locatorPath) {
        File locatorsDir = new File(locatorPath);
            if (locatorsDir.exists() && locatorsDir.isDirectory()) {
                int count = scanDirectory(locatorsDir);
                Logger.info("[Healing] Initialized with %d locator files from: %s", count, locatorsDir.getAbsolutePath());
            } else {
                Logger.warn("[Healing] Locators directory not found at: %s (Abs: %s)", locatorPath, locatorsDir.getAbsolutePath());
            }
    }

    private static int scanDirectory(File dir) {
        File[] files = dir.listFiles();
        int count = 0;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    count += scanDirectory(file);
                } else if (file.getName().endsWith(".json")) {
                    init(file.getPath());
                    count++;
                }
            }
        }
        return count;
    }

    public static void init(String locatorJsonPath) {
        try {
            LocatorRepository repo = LocatorRepository.getInstance();
            repo.loadFromFile(locatorJsonPath);

            LocatorMapper mapper = LocatorMapper.getInstance();
            mapper.buildIndex();
            
            
            Logger.debug("Loaded locators from: %s", locatorJsonPath);
        } catch (Exception e) {
            Logger.error("Failed to load locators: %s", e.getMessage());
        }
    }
}

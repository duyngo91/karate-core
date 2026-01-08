package core.healing;

import core.platform.utils.Logger;
import java.io.File;

public class HealingInitializer {
    private static boolean initialized = false;

    public static void autoInit() {
        String autoHealing = System.getProperty("auto.healing");
        String locatorPath = System.getProperty("locator.path");

        if ("true".equalsIgnoreCase(autoHealing) && locatorPath != null) {
            Logger.info("Enable auto-healing");
            loadLocatorsFromPath(locatorPath);

            // Register shutdown hook for monitoring report
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                HealingMonitor.getInstance().generateReport();
            }));
        }
    }

    private static void loadLocatorsFromPath(String locatorPath) {
        File locatorsDir = new File(locatorPath);
        if (locatorsDir.exists() && locatorsDir.isDirectory()) {
            int count = scanDirectory(locatorsDir);
            Logger.info("Self-healing initialized with %d locator files from: %s", count, locatorPath);
        } else {
            Logger.warn("Locators directory not found at: %s - self-healing disabled", locatorPath);
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

            if (!initialized) {
                LocatorHistory history = new LocatorHistory();
                history.load();
                initialized = true;
            }

            Logger.debug("Loaded locators from: %s", locatorJsonPath);
        } catch (Exception e) {
            Logger.error("Failed to load locators: %s", e.getMessage());
        }
    }
}

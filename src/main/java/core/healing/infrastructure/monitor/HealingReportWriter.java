package core.healing.infrastructure.monitor;

import core.platform.utils.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class HealingReportWriter {

    public void write(String content, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            Logger.info("Healing report generated at: %s", path);
        } catch (IOException e) {
            Logger.error("Failed to write healing report: %s", e.getMessage());
        }
    }
}

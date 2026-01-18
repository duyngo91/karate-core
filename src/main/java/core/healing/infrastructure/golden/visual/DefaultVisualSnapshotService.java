package core.healing.infrastructure.golden.visual;

import core.healing.application.port.IHealingDriver;
import core.healing.infrastructure.config.HealingConfig;
import core.healing.infrastructure.visual.VisualService;

import java.awt.image.BufferedImage;

public class DefaultVisualSnapshotService implements VisualSnapshotService {

    @Override
    public void capture(IHealingDriver driver, String elementId, String locator) {
        if (!HealingConfig.getInstance().getStrategies().contains("VisualHealingStrategy")) {
            return;
        }

        VisualService visual = VisualService.getInstance();
        if (!visual.isAvailable()) return;

        BufferedImage image = visual.captureScreenshot(driver, locator);
        if (image != null) {
            visual.saveScreenshot(image, elementId);
        }
    }
}

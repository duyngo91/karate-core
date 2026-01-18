package core.healing.infrastructure.golden.visual;

import core.healing.application.port.IHealingDriver;

public interface VisualSnapshotService {
    void capture(IHealingDriver driver, String elementId, String locator);
}


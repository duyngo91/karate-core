package core.healing.infrastructure.golden.visual;

import core.healing.IHealingDriver;

public interface VisualSnapshotService {
    void capture(IHealingDriver driver, String elementId, String locator);
}


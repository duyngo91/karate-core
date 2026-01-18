package core.healing.infrastructure.golden.extractor;


import core.healing.application.port.IHealingDriver;
import core.healing.domain.model.ElementMetadata;

public interface DomSnapshotExtractor {
    ElementMetadata extract(IHealingDriver driver, String elementId, String locator);
}


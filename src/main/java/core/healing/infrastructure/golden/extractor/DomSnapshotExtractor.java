package core.healing.infrastructure.golden.extractor;


import core.healing.IHealingDriver;
import core.healing.rag.ElementMetadata;

public interface DomSnapshotExtractor {
    ElementMetadata extract(IHealingDriver driver, String elementId, String locator);
}


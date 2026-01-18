package core.healing.infrastructure.rag;

import core.healing.application.port.IHealingDriver;
import core.healing.application.port.GoldenStateStore;
import core.healing.infrastructure.golden.GoldenStateRecorder;
import core.healing.infrastructure.golden.repository.GoldenStateRepository;
import core.healing.domain.model.ElementMetadata;

/**
 * Adapter: Domain → Golden infra
 */
public class RagGoldenStateStore implements GoldenStateStore {

    private final GoldenStateRecorder recorder;
    private final GoldenStateRepository repository;

    public RagGoldenStateStore(
            GoldenStateRecorder recorder,
            GoldenStateRepository repository
    ) {
        this.recorder = recorder;
        this.repository = repository;
    }

    @Override
    public String findLearnedLocator(String elementId) {
        if (elementId == null) return null;

        ElementMetadata meta = getMetadata(elementId);
        return meta != null ? meta.getLocator() : null;
    }

    @Override
    public ElementMetadata getMetadata(String elementId) {
        return repository.find(elementId);
    }

    @Override
    public void recordSuccess(String elementId, String locator, IHealingDriver driver) {
        if (elementId == null || locator == null || driver == null) return;
        recorder.record(driver, elementId, locator);
    }
}

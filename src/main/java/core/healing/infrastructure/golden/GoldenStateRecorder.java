package core.healing.infrastructure.golden;

import core.healing.application.port.IHealingDriver;
import core.healing.infrastructure.embedding.EmbeddingGenerator;
import core.healing.infrastructure.golden.extractor.DomSnapshotExtractor;
import core.healing.infrastructure.golden.repository.GoldenStateRepository;
import core.healing.infrastructure.golden.visual.VisualSnapshotService;
import core.healing.domain.model.ElementMetadata;
import core.platform.utils.Logger;

/**
 * Capture and store "Golden State" metadata for elements when they are
 * successfully found.
 */
public class GoldenStateRecorder {

    private final DomSnapshotExtractor extractor;
    private final EmbeddingGenerator embeddingGenerator;
    private final VisualSnapshotService visualService;
    private final GoldenStateRepository repository;

    public GoldenStateRecorder(
            DomSnapshotExtractor extractor,
            EmbeddingGenerator embeddingGenerator,
            VisualSnapshotService visualService,
            GoldenStateRepository repository
    ) {
        this.extractor = extractor;
        this.embeddingGenerator = embeddingGenerator;
        this.visualService = visualService;
        this.repository = repository;
    }

    public void record(IHealingDriver driver, String elementId, String locator) {

        ElementMetadata meta = extractor.extract(driver, elementId, locator);
        if (meta == null) return;

        String context = ElementMetadata.buildEmbed(
                meta.getTagName(),
                meta.getText(),
                meta.getAttributes(),
                meta.getNeighborText()
        );

        try {
            meta.setVector(embeddingGenerator.embed(context));
        } catch (Exception e) {
            Logger.warn("Embedding failed: %s", e.getMessage());
        }

        visualService.capture(driver, elementId, locator);

        repository.save(elementId, meta);
    }
}


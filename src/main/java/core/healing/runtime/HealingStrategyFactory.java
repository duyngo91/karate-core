package core.healing.runtime;

import core.healing.application.port.GoldenStateStore;
import core.healing.domain.strategy.*;
import core.healing.infrastructure.embedding.EmbeddingService;

public class HealingStrategyFactory {

    private final GoldenStateStore store;
    private final EmbeddingService embeddingService;

    public HealingStrategyFactory(
            GoldenStateStore store,
            EmbeddingService embeddingService) {
        this.store = store;
        this.embeddingService = embeddingService;
    }

    public HealingStrategy create(String name) {
        return switch (name) {
            case "ExactAttributeStrategy" -> new ExactAttributeStrategy();
            case "KeyBasedStrategy" -> new KeyBasedStrategy();
            case "TextBasedStrategy" -> new TextBasedStrategy();
            case "CrossAttributeStrategy" -> new CrossAttributeStrategy();
            case "SemanticValueStrategy" -> new SemanticValueStrategy();
            case "StructuralStrategy" -> new StructuralStrategy();
            case "NeighborStrategy" -> new NeighborStrategy();
            case "RagHealingStrategy" ->
                    new RagHealingStrategy(store, embeddingService);
            case "VisualHealingStrategy" ->
                    new VisualHealingStrategy();
            default -> null;
        };
    }
}


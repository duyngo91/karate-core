package core.healing.domain.strategy;

import core.healing.HealingConfig;
import core.healing.domain.port.GoldenStateStore;
import core.healing.rag.EmbeddingService;
import core.platform.utils.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HealingStrategyRegistry {

    private final List<HealingStrategy> strategies = new ArrayList<>();

    public List<HealingStrategy> getStrategies() {
        return strategies;
    }

    public HealingStrategyRegistry register(HealingStrategy s) {
        strategies.add(s);
        return this;
    }

    public List<HealingStrategy> ordered() {
        return strategies.stream()
                .sorted(Comparator.comparingDouble(HealingStrategy::getWeight).reversed())
                .toList();
    }

    public void registerAll(GoldenStateStore goldenStateStore) {

        List<String> configStrategies =
                HealingConfig.getInstance().getStrategies();

        if (configStrategies != null && !configStrategies.isEmpty()) {
            for (String name : configStrategies) {
                HealingStrategy s = createStrategy(name, goldenStateStore);
                if (s != null) register(s);
            }
            return;
        }

        // Default
        register(new ExactAttributeStrategy());
        register(new KeyBasedStrategy());
        register(new TextBasedStrategy());
        register(new CrossAttributeStrategy());
        register(new SemanticValueStrategy());
        register(new StructuralStrategy());
        register(new NeighborStrategy());
        register(new RagHealingStrategy(
                goldenStateStore,
                EmbeddingService.getInstance()
        ));
    }


    private HealingStrategy createStrategy(String name, GoldenStateStore goldenStateStore) {
        return switch (name) {
            case "ExactAttributeStrategy" -> new ExactAttributeStrategy();
            case "KeyBasedStrategy" -> new KeyBasedStrategy();
            case "TextBasedStrategy" -> new TextBasedStrategy();
            case "CrossAttributeStrategy" -> new CrossAttributeStrategy();
            case "SemanticValueStrategy" -> new SemanticValueStrategy();
            case "StructuralStrategy" -> new StructuralStrategy();
            case "NeighborStrategy" -> new NeighborStrategy();
            case "RagHealingStrategy" -> new RagHealingStrategy(goldenStateStore, EmbeddingService.getInstance());
            case "VisualHealingStrategy" -> new VisualHealingStrategy();
            default -> {
                Logger.warn("Unknown strategy in config: %s", name);
                yield null;
            }
        };
    }
}

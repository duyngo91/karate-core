package core.healing.runtime;

import core.healing.domain.strategy.HealingStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HealingStrategyRegistry {

    private final List<HealingStrategy> strategies = new ArrayList<>();

    public HealingStrategyRegistry register(HealingStrategy s) {
        strategies.add(s);
        return this;
    }

    public List<HealingStrategy> all() {
        return List.copyOf(strategies);
    }

    public List<HealingStrategy> ordered() {
        return strategies.stream()
                .sorted(Comparator.comparingDouble(HealingStrategy::getWeight).reversed())
                .toList();
    }
}


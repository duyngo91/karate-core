package core.healing.infrastructure;

import core.healing.application.HealingStore;
import core.healing.runtime.HealingCache;

public class InMemoryHealingStore implements HealingStore {

    private final HealingCache cache;

    public InMemoryHealingStore(HealingCache cache) {
        this.cache = cache;
    }

    @Override
    public String get(String locator) {
        return cache.get(locator);
    }

    @Override
    public void save(String locator, String healed) {
        cache.put(locator, healed);
    }
}

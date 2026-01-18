package core.healing.application;


public interface HealingStore {
    String get(String elementId);
    void save(String locator, String healed);
}

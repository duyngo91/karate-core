package core.healing.application.port;

import core.healing.domain.model.ElementMetadata;

/**
 * Domain Port
 * -----------------
 * Abstraction for accessing learned / golden locators.
 *
 * Domain & Application depend on this interface,
 * Infrastructure provides implementation.
 */
public interface GoldenStateStore {

    /**
     * Find a previously learned locator for an element.
     *
     * @param elementId logical element id (e.g. login.username)
     * @return learned locator or null if not found
     */
    String findLearnedLocator(String elementId);
    ElementMetadata getMetadata(String elementId);

    /**
     * Record a successful healing result.
     *
     * @param elementId element logical id
     * @param locator healed locator
     * @param driver driver used to capture golden state
     */
    void recordSuccess(String elementId, String locator, IHealingDriver driver);
}


package core.healing.application.port;

import core.healing.domain.model.HealingEvent;

public interface HealingMonitor {

    void onEvent(HealingEvent event);

    /**
     * Called when healing lifecycle ends (JVM shutdown / test end)
     */
    default void onFinish() {
        // default no-op
    }

    HealingMonitor NO_OP = new HealingMonitor() {
        @Override
        public void onEvent(HealingEvent event) {
            // do nothing
        }

        @Override
        public void onFinish() {
            // do nothing
        }
    };
}

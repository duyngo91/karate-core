package core.healing.infrastructure.monitor;

import core.healing.application.port.HealingMonitor;
import core.healing.domain.model.HealingEvent;
import core.platform.utils.Logger;

public class LogHealingMonitor implements HealingMonitor {

    public LogHealingMonitor() {
    }
    @Override
    public void onEvent(HealingEvent event) {
        Logger.info(
                "[Healing] %s | %s -> %s | score=%.2f | strategy=%s (Success: %s) (time: %s)",
                event.elementId,
                event.original,
                event.healed,
                event.score,
                event.method,
                event.success,
                event.timestamp
        );
    }

    @Override
    public void onFinish() {

    }
}


package core.healing.infrastructure.adapter.monitor;

import core.healing.application.port.HealingMonitor;
import core.healing.domain.model.HealingEvent;

import java.util.ArrayList;
import java.util.List;

public class CompositeHealingMonitor implements HealingMonitor {

    private final List<HealingMonitor> monitors = new ArrayList<>();

    public CompositeHealingMonitor(List<HealingMonitor> monitors) {
        if (monitors != null) {
            this.monitors.addAll(monitors);
        }
    }

    public void add(HealingMonitor monitor) {
        monitors.add(monitor);
    }

    @Override
    public void onEvent(HealingEvent event) {
        for (HealingMonitor monitor : monitors) {
            try {
                monitor.onEvent(event);
            } catch (Exception e) {
                // ⛔ monitor lỗi KHÔNG làm chết healing
                System.err.println(
                        "[HealingMonitor] Failed: " + monitor.getClass().getSimpleName()
                );
            }
        }
    }

    @Override
    public void onFinish() {
        monitors.forEach(HealingMonitor::onFinish);
    }
}

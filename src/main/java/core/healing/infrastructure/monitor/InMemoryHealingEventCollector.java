package core.healing.infrastructure.monitor;

import  core.healing.application.port.HealingMonitor;
import  core.healing.domain.model.HealingEvent;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHealingEventCollector implements HealingMonitor {

    private final List<HealingEvent> events = new ArrayList<>();


    @Override
    public void onEvent(HealingEvent event) {
        events.add(event);
    }

    @Override
    public void onFinish() {

    }

    public List<HealingEvent> getEvents() {
        return events;
    }
}

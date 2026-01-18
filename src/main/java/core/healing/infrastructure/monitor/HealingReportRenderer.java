package core.healing.infrastructure.monitor;

import core.healing.domain.model.HealingEvent;

import java.util.List;

public interface HealingReportRenderer {
    String render(List<HealingEvent> events);
}


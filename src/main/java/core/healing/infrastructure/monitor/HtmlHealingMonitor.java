package core.healing.infrastructure.monitor;

import core.healing.application.port.HealingMonitor;
import core.healing.domain.model.HealingEvent;

import java.util.ArrayList;
import java.util.List;

public class HtmlHealingMonitor implements HealingMonitor {

    private final List<HealingEvent> events = new ArrayList<>();
    private final HealingReportRenderer renderer;
    private final HealingReportWriter writer;
    private final String outputPath;

    public HtmlHealingMonitor(String outputPath) {
        this.renderer = new HtmlReportRenderer(new HtmlReportViewBuilder());
        this.writer = new HealingReportWriter();
        this.outputPath = outputPath;
    }

    @Override
    public void onEvent(HealingEvent event) {
        events.add(event);
    }

    @Override
    public void onFinish() {
        String html = renderer.render(events);
        writer.write(html, outputPath);
    }
}

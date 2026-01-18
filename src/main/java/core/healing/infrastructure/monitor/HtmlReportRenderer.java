package core.healing.infrastructure.monitor;

import core.healing.domain.model.HealingEvent;
import java.util.List;

public class HtmlReportRenderer implements HealingReportRenderer {

    private final HtmlReportViewBuilder viewBuilder;

    public HtmlReportRenderer(HtmlReportViewBuilder viewBuilder) {
        this.viewBuilder = viewBuilder;
    }

    @Override
    public String render(List<HealingEvent> events) {
        return viewBuilder.build(events);
    }
}

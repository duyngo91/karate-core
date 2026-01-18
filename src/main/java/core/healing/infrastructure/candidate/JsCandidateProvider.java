package core.healing.infrastructure.candidate;

import core.healing.application.candidate.JsCandidateCollector;
import core.healing.application.port.CandidateProvider;
import core.healing.application.port.IHealingDriver;
import core.healing.domain.model.ElementNode;

import java.util.List;

/**
 * Infrastructure adapter: browser-based candidate provider.
 */
public class JsCandidateProvider implements CandidateProvider {

    private final JsCandidateCollector collector;

    public JsCandidateProvider(IHealingDriver driver) {
        this.collector = new JsCandidateCollector(driver);
    }

    @Override
    public List<ElementNode> getCandidates() {
        return collector.collect();
    }
}

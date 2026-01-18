package core.healing.application.port;

import core.healing.domain.model.ElementNode;
import java.util.List;

/**
 * Port to provide candidate elements for healing.
 * Application depends on this, Infra implements it.
 */
public interface CandidateProvider {

    /**
     * Collect candidate elements from current UI context.
     */
    List<ElementNode> getCandidates();
}


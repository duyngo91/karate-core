package core.healing.application.port;

import core.healing.domain.model.ElementNode;

public interface CandidateExtractor {
    ElementNode extract(String elementId, String locator);
}


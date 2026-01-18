package core.healing.domain;

import core.healing.domain.model.ElementNode;

public interface CandidateExtractor {
    ElementNode extract(String elementId, String locator);
}


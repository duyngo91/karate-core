package core.healing.infrastructure.golden.repository;


import core.healing.rag.ElementMetadata;

import java.util.Map;

public interface GoldenStateRepository {
    void save(String elementId, ElementMetadata metadata);
    ElementMetadata find(String elementId);
    Map<String, ElementMetadata> findAll();
    void clear();
}


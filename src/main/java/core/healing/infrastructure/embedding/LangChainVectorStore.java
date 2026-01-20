package core.healing.infrastructure.embedding;

import core.healing.application.port.VectorStoreAdapter;
import core.platform.utils.Logger;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of VectorStoreAdapter using LangChain4j's InMemoryEmbeddingStore.
 */
public class LangChainVectorStore implements VectorStoreAdapter {

    private final EmbeddingStore<TextSegment> embeddingStore;

    public LangChainVectorStore() {
        this.embeddingStore = new InMemoryEmbeddingStore<>();
    }

    @Override
    public void upsertElement(String elementId, float[] vector, Map<String, String> metadataMap) {
        Logger.info("Upserting element to Vector Store: {}", elementId);
        Embedding embedding = Embedding.from(vector);
        
        Map<String, Object> map = new HashMap<>(metadataMap);
        map.put("elementId", elementId);
        Metadata metadata = Metadata.from(map);
        
        TextSegment segment = TextSegment.from(elementId, metadata);
        embeddingStore.add(embedding, segment);
    }

    @Override
    public List<SimilarityResult> searchSimilar(float[] vector, int topK) {
        Embedding queryEmbedding = Embedding.from(vector);
        
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(topK)
                .minScore(0.0)
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
        List<EmbeddingMatch<TextSegment>> matches = result.matches();

        return matches.stream()
                .map(match -> new SimilarityResult(
                        match.embedded().metadata().getString("elementId"),
                        match.score(),
                        match.embedded().metadata().toMap().entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())))
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void updateSuccessRate(String elementId, boolean success) {
        // In a real DB, we would update metadata. 
        // For InMemoryEmbeddingStore, we might need to recreate the entry if we want to update.
    }
}

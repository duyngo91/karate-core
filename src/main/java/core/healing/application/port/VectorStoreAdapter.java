package core.healing.application.port;

import java.util.List;
import java.util.Map;

/**
 * Interface to replace static JSON file for Learning & Memory.
 * Connects to a Vector DB (Milvus, Chroma, Pinecone, etc.)
 */
public interface VectorStoreAdapter {

    /**
     * Stores an element's embedding and metadata.
     */
    void upsertElement(String elementId, float[] vector, Map<String, String> metadata);

    /**
     * Searches for the most similar elements based on a vector.
     */
    List<SimilarityResult> searchSimilar(float[] vector, int topK);

    /**
     * Purges or updates memory based on success/failure.
     */
    void updateSuccessRate(String elementId, boolean success);

    record SimilarityResult(String elementId, double score, Map<String, String> metadata) {}
}

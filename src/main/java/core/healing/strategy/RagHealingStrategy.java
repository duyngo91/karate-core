package core.healing.strategy;

import core.healing.model.ElementNode;
import core.healing.rag.ElementMetadata;
import core.healing.rag.EmbeddingService;
import core.healing.rag.GoldenStateRecorder;

/**
 * Strategy 8: RAG Healing (Vector Similarity)
 * Uses vector embeddings to match elements based on semantic similarity.
 */
public class RagHealingStrategy implements HealingStrategy {

    @Override
    public double score(ElementNode original, ElementNode candidate) {
        // 1. Get Golden State (Metadata + Vector) for the target element
        String key = original.getElementId();

        // Fallback to name/id if elementId is missing (though it shouldn't be now)
        if (key == null) {
            key = original.getAttribute("id");
        }
        if (key == null) {
            key = original.getAttribute("name");
        }

        if (key == null)
            return 0.0;

        ElementMetadata metadata = GoldenStateRecorder.getInstance().getMetadata(key);
        if (metadata == null || metadata.getVector() == null) {
            return 0.0; // No historical data to compare against
        }

        // 2. Generate Vector for Candidate (if not already cached)
        // Optimization: In a real system, we might want to batch this or cache it on
        // the candidate itself.
        // For now, we generate on fly.
        float[] candidateVector = getOrGenerateVector(candidate);

        if (candidateVector == null || candidateVector.length == 0)
            return 0.0;

        // 3. Cosine Similarity
        return cosineSimilarity(metadata.getVector(), candidateVector);
    }

    private float[] getOrGenerateVector(ElementNode node) {
        String context = "Tag: " + node.getTagName() +
                ", Text: " + (node.getText() != null ? node.getText() : "") +
                ", Attributes: " + node.getAttributes() +
                ", Neighbor: " + (node.getPrevSiblingText() != null ? node.getPrevSiblingText() : "");

        return EmbeddingService.getInstance().embed(context);
    }

    private double cosineSimilarity(float[] v1, float[] v2) {
        if (v1.length != v2.length)
            return 0.0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            normA += v1[i] * v1[i];
            normB += v2[i] * v2[i];
        }

        if (normA == 0 || normB == 0)
            return 0.0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public String getName() {
        return "RAG/VectorStrategy";
    }

    @Override
    public double getWeight() {
        return 0.95; // High confidence if vector matches
    }
    @Override
    public double getHealingHold() {
        return 0.5;
    }
}

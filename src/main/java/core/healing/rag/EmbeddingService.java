package core.healing.rag;

import core.platform.utils.Logger;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;

/**
 * Service to generate embeddings using LangChain4j and a local ONNX model.
 * https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/embedding/model/InProcessEmbeddingModelExamples.java
 */
public class EmbeddingService {
    private static EmbeddingService instance;
    private EmbeddingModel embeddingModel;

    private EmbeddingService() {
        try {
            // Initialize local embedding model (runs in-process, no API calls)
            this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
            Logger.info("Embedding Model (AllMiniLmL6V2) initialized.");
        } catch (Exception e) {
            Logger.error("Failed to initialize Embedding Model: %s", e.getMessage());
        }
    }

    public static synchronized EmbeddingService getInstance() {
        if (instance == null) {
            instance = new EmbeddingService();
        }
        return instance;
    }

    public float[] embed(String text) {
        if (embeddingModel == null || text == null || text.isEmpty())
            return new float[0];
        try {
            // Generate embedding
            return embeddingModel.embed(text).content().vector();
        } catch (Exception e) {
            Logger.error("Embedding generation failed: %s", e.getMessage());
            return new float[0];
        }
    }
}

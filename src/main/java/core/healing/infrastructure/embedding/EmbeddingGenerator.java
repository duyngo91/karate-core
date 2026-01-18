package core.healing.infrastructure.embedding;


public interface EmbeddingGenerator {
    float[] embed(String context);
}

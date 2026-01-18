package core.healing.infrastructure.golden.embedding;


public interface EmbeddingGenerator {
    float[] embed(String context);
}

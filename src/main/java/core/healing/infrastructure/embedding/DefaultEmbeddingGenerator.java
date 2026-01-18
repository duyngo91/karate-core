package core.healing.infrastructure.embedding;

public class DefaultEmbeddingGenerator implements EmbeddingGenerator {

    @Override
    public float[] embed(String context) {
        return EmbeddingService.getInstance().embed(context);
    }
}


package core.healing.infrastructure.golden.embedding;

import core.healing.rag.EmbeddingService;

public class DefaultEmbeddingGenerator implements EmbeddingGenerator {

    @Override
    public float[] embed(String context) {
        return EmbeddingService.getInstance().embed(context);
    }
}


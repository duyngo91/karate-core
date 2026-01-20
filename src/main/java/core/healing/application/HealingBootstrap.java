package core.healing.application;

import core.healing.application.port.CandidateProvider;
import core.healing.application.port.IHealingDriver;
import core.healing.application.port.VectorStoreAdapter;
import core.healing.infrastructure.InMemoryHealingStore;
import core.healing.infrastructure.adapter.candidate.DefaultCandidateExtractor;
import core.healing.infrastructure.adapter.candidate.JsCandidateProvider;
import core.healing.infrastructure.config.HealingConfig;
import core.healing.infrastructure.embedding.ElementMigrationService;
import core.healing.runtime.HealingCache;
import core.healing.runtime.HealingRuntime;
import core.intelligence.FailureAnalyzer;
import core.intelligence.LangChainAIProvider;
import core.platform.utils.Logger;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public final class HealingBootstrap {

    public static SelfHealingDriver create(IHealingDriver driver) {

        HealingConfig config = HealingConfig.getInstance();

        // DISABLED MODE
        if (!config.isEnabled()) {
            return new SelfHealingDriver(driver, null, false);
        }

        // ENABLED MODE
        HealingRuntime.start();
        HealingRuntime runtime = HealingRuntime.get();

        HealingStore store =
                new InMemoryHealingStore(HealingCache.getInstance());

        CandidateProvider candidateProvider =
                new JsCandidateProvider(driver);

        // VECTOR MEMORY & MIGRATION
        VectorStoreAdapter vectorStore = runtime.vectorStore();
        if (vectorStore != null) {
            ElementMigrationService migrationService = new ElementMigrationService(vectorStore);
            migrationService.migrate("element-metadata.json");
        }

        FailureAnalyzer failureAnalyzer = null;
        if (config.isFailureIntelligenceEnabled()) {
            FailureAnalyzer.AIProvider provider = null;
            String apiKey = config.getAiApiKey();
            String modelName = config.getAiModel();

            if (apiKey != null && !apiKey.isEmpty()) {
                ChatModel model;
                if (modelName.contains("gpt")) {
                    model = OpenAiChatModel.builder()
                            .apiKey(apiKey)
                            .modelName(modelName)
                            .timeout(Duration.ofSeconds(60))
                            .build();
                } else {
                    model = GoogleAiGeminiChatModel.builder()
                            .apiKey(apiKey)
                            .modelName(modelName)
                            .build();
                }
                provider = new LangChainAIProvider(model);
                Logger.info("AI Provider initialized: {}", modelName);
            } else {
                Logger.warn("AI API Key missing. Failure Intelligence will run in mock mode.");
            }
            failureAnalyzer = new FailureAnalyzer(provider);
        }

        HealingOrchestrator orchestrator =
                new HealingOrchestrator(
                        store,
                        runtime.goldenStateStore(),
                        runtime.engine(),
                        new DefaultCandidateExtractor(),
                        candidateProvider,
                        runtime.monitor(),
                        failureAnalyzer,
                        vectorStore
                );

        return new SelfHealingDriver(driver, orchestrator, true);
    }
}


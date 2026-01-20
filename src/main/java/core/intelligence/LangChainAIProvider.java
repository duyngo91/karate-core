package core.intelligence;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import core.platform.utils.Logger;

/**
 * Implementation of AIProvider using LangChain4j.
 */
public class LangChainAIProvider implements FailureAnalyzer.AIProvider {

    private final ChatModel chatModel;

    public LangChainAIProvider(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public DiagnosticReport generateDiagnosis(String htmlContext, String errorContext) {
        Logger.info("Generating AI Diagnosis using LangChain...");
        
        String prompt = String.format(
                "You are an expert Automation QA engineer. Analyze the following UI test failure.\n" +
                "Error: %s\n" +
                "Target HTML Context: %s\n\n" +
                "Provide a detailed analysis and suggestion on how to fix the locator.",
                errorContext, htmlContext
        );

        // In 1.10.0, chatModel.chat(prompt) returns a String or we can use chat(ChatRequest)
        // Based on the provided class file, it's safer to use the higher level chat() method
        String aiResponse = chatModel.chat(prompt);

        Logger.debug("AI Response: {}", aiResponse);
        
        return DiagnosticReport.builder()
                .analysis(aiResponse)
                .failureType("AI_DIAGNOSED")
                .confidence(0.9)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}

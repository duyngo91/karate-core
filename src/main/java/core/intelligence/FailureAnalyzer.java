package core.intelligence;

import core.healing.application.port.IHealingDriver;
import core.platform.utils.Logger;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Prototype for the Failure Intelligence Layer.
 * This class gathers context and calls an AI provider to diagnose the failure.
 */
@Slf4j
public class FailureAnalyzer {

    private final AIProvider aiProvider;

    public FailureAnalyzer(AIProvider aiProvider) {
        this.aiProvider = aiProvider;
    }

    /**
     * Analyzes a failure by collecting current page state and exception details.
     */
    public DiagnosticReport analyze(String locator, Exception originalException, IHealingDriver driver) {
        if (aiProvider == null) {
            Logger.warn("AI Provider not configured. Returning basic report.");
            return DiagnosticReport.builder()
                    .analysis("AI Analysis disabled or not configured.")
                    .timestamp(System.currentTimeMillis())
                    .build();
        }

        Logger.info("Starting AI Failure Analysis for locator: {}", locator);
        
        // 1. Collect Context (Smart fallback if element is missing)
        String htmlContext;
        try {
            htmlContext = driver.getOuterHtml(locator);
            if (htmlContext == null || htmlContext.isEmpty()) {
                throw new RuntimeException("Element not found");
            }
        } catch (Exception e) {
            Logger.warn("Could not capture outerHtml for broken locator. Capturing body context instead.");
            // Fallback: Capture the whole body or a large chunk of the DOM to let AI find the element
            htmlContext = "ELEMENT_MISSING_IN_DOM. Page Context: " + driver.getOptimizedPageSource();
        }
        
        String errorMessage = originalException.getMessage();

        // 2. Real AI Analysis
        return aiProvider.generateDiagnosis(htmlContext, errorMessage);
    }

    public interface AIProvider {
        DiagnosticReport generateDiagnosis(String htmlContext, String errorContext);
    }
}

package core.healing;

import core.platform.web.ChromeCustom;
import core.platform.utils.Logger;
import org.apache.commons.text.similarity.*;
import java.util.*;

public class SemanticLocator {
    private final ChromeCustom driver;
    private final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();
    private final JaccardSimilarity jaccard = new JaccardSimilarity();
    private static final double SIMILARITY_THRESHOLD = 0.4; // 10% similarity
    
    public SemanticLocator(ChromeCustom driver) {
        this.driver = driver;
    }
    
    private double calculateBestSimilarity(String s1, String s2) {
        double jaro = jaroWinkler.apply(s1, s2);
        
        // Normalize Levenshtein to 0-1 range
        int maxLen = Math.max(s1.length(), s2.length());
        double leven = maxLen == 0 ? 1.0 : 1.0 - (double) levenshtein.apply(s1, s2) / maxLen;
        
        double jacc = jaccard.apply(s1, s2);
        
        // Return best score from 3 algorithms
        return Math.max(Math.max(jaro, leven), jacc);
    }
    
    public String findBySemantics(String intent) {
        Logger.debug("Finding element by semantic intent: %s", intent);
        
        // Extract last key from dotted notation (login.inpUserName -> inpUserName)
        String cleanIntent = extractLastKey(intent);
        Logger.debug("Cleaned intent: %s", cleanIntent);
        
        String ariaLocator = findByAria(cleanIntent);
        if (ariaLocator != null) return ariaLocator;
        
        String roleLocator = findByRole(cleanIntent);
        if (roleLocator != null) return roleLocator;
        
        String testIdLocator = findByTestId(cleanIntent);
        if (testIdLocator != null) return testIdLocator;
        
        String semanticLocator = findBySemanticHtml(cleanIntent);
        if (semanticLocator != null) return semanticLocator;
        
        String fuzzyLocator = findByFuzzyText(cleanIntent);
        if (fuzzyLocator != null) return fuzzyLocator;
        
        return null;
    }
    
    private String extractLastKey(String intent) {
        if (intent == null || intent.isEmpty()) return intent;
        
        // Handle dotted notation: login.inpUserName -> inpUserName
        if (intent.contains(".")) {
            String[] parts = intent.split("\\.");
            return parts[parts.length - 1];
        }
        
        return intent;
    }
    
    private String findByAria(String intent) {
        String[] ariaAttrs = {"aria-label", "aria-labelledby", "aria-describedby"};
        
        for (String attr : ariaAttrs) {
            String locator = String.format("//*[@%s='%s']", attr, intent);
            if (driver.exist(locator)) return locator;
            
            locator = String.format("//*[contains(@%s, '%s')]", attr, intent);
            if (driver.exist(locator)) return locator;
        }
        return null;
    }
    
    private String findByRole(String intent) {
        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("button", "button");
        roleMap.put("link", "link");
        roleMap.put("input", "textbox");
        roleMap.put("txt", "textbox");
        roleMap.put("checkbox", "checkbox");
        roleMap.put("dropdown", "combobox");
        roleMap.put("dl", "combobox");

        for (Map.Entry<String, String> entry : roleMap.entrySet()) {
            if (intent.toLowerCase().contains(entry.getKey())) {
                String locator = String.format("//*[@role='%s' and contains(., '%s')]", 
                    entry.getValue(), intent);
                if (driver.exist(locator)) return locator;
            }
        }
        return null;
    }
    
    private String findByTestId(String intent) {
        String[] testIdAttrs = {"data-testid", "data-test", "data-qa", "data-cy"};
        String normalized = intent.toLowerCase().replaceAll("\\s+", "-");
        
        for (String attr : testIdAttrs) {
            String locator = String.format("//*[@%s='%s']", attr, normalized);
            if (driver.exist(locator)) return locator;
        }
        return null;
    }
    
    private String findBySemanticHtml(String intent) {
        String lower = intent.toLowerCase();
        
        if (lower.contains("button")) {
            String locator = String.format("//button[contains(text(), '%s')]", intent);
            if (driver.exist(locator)) return locator;
        }
        
        if (lower.contains("link")) {
            String locator = String.format("//a[contains(text(), '%s')]", intent);
            if (driver.exist(locator)) return locator;
        }
        
        if (lower.contains("input")) {
            String locator = String.format("//input[@placeholder='%s' or @name='%s']", intent, intent);
            if (driver.exist(locator)) return locator;
        }
        
        return null;
    }
    
    private String findByFuzzyText(String intent) {
        List<ElementCandidate> candidates = new ArrayList<>();
        
        // Get page source and parse DOM intelligently
        String pageSource = driver.getPageSource();
        List<ElementInfo> elements = parseInteractiveElements(pageSource);
        
        for (ElementInfo element : elements) {
            double score = calculateElementScore(element, intent);
            candidates.add(new ElementCandidate(element.locator, score, element.description));
        }
        
        // Sort by score and try top 2 candidates
        candidates.sort((a, b) -> Double.compare(b.score, a.score));
        
        // Try best 2 candidates
        for (int i = 0; i < Math.min(2, candidates.size()); i++) {
            ElementCandidate candidate = candidates.get(i);
            if (candidate.score >= SIMILARITY_THRESHOLD && driver.exist(candidate.locator)) {
                Logger.debug("Fuzzy match #%d: %s (score: %.2f)", i+1, candidate.description, candidate.score);
                return candidate.locator;
            }
        }
        
        return null;
    }
    
    private List<ElementInfo> parseInteractiveElements(String html) {
        List<ElementInfo> elements = new ArrayList<>();
        
        // Parse buttons, links, inputs with attributes
        String[] patterns = {
            "<button[^>]*>([^<]*)</button>",
            "<a[^>]*>([^<]*)</a>", 
            "<input[^>]*>",
            "<select[^>]*>",
            "<textarea[^>]*>"
        };
        
        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(html);
            
            while (m.find()) {
                ElementInfo info = extractElementInfo(m.group());
                if (info != null) elements.add(info);
            }
        }
        
        return elements;
    }
    
    private ElementInfo extractElementInfo(String elementHtml) {
        ElementInfo info = new ElementInfo();
        
        // Extract text content
        java.util.regex.Pattern textPattern = java.util.regex.Pattern.compile(">([^<]+)<");
        java.util.regex.Matcher textMatcher = textPattern.matcher(elementHtml);
        if (textMatcher.find()) {
            info.text = textMatcher.group(1).trim();
        }
        
        // Extract key attributes
        info.id = extractAttribute(elementHtml, "id");
        info.name = extractAttribute(elementHtml, "name");
        info.className = extractAttribute(elementHtml, "class");
        info.ariaLabel = extractAttribute(elementHtml, "aria-label");
        info.placeholder = extractAttribute(elementHtml, "placeholder");
        info.testId = extractAttribute(elementHtml, "data-testid");
        
        // Build optimal locator
        info.locator = buildLocator(info);
        info.description = buildDescription(info);
        
        return info;
    }
    
    private String extractAttribute(String html, String attrName) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            attrName + "\\s*=\\s*[\"']([^\"']*)[\"']", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    private String buildLocator(ElementInfo info) {
        if (info.id != null && !info.id.isEmpty()) {
            return "//*[@id='" + info.id + "']";
        }

        if (info.name != null && !info.name.isEmpty()) {
            return "//*[@name='" + info.name + "']";
        }

        if (info.testId != null && !info.testId.isEmpty()) {
            return "//*[@data-testid='" + info.testId + "']";
        }
        if (info.text != null && !info.text.isEmpty()) {
            return "//*[contains(text(), '" + info.text + "')]";
        }

        return null;
    }
    
    private String buildDescription(ElementInfo info) {
        StringBuilder desc = new StringBuilder();
        if (info.text != null) desc.append("text:").append(info.text).append(" ");
        if (info.ariaLabel != null) desc.append("aria:").append(info.ariaLabel).append(" ");
        if (info.placeholder != null) desc.append("placeholder:").append(info.placeholder);
        return desc.toString().trim();
    }
    
    private double calculateElementScore(ElementInfo element, String intent) {
        double score = 0;
        
        // Extract last key from intent (login.inpUserName -> inpUserName)
        String cleanIntent = extractLastKey(intent);
        String intentLower = cleanIntent.toLowerCase();
        
        // Smart camelCase/snake_case normalization
        String normalizedIntent = normalizeIdentifier(intentLower);
        String normalizedId = element.id != null ? normalizeIdentifier(element.id.toLowerCase()) : "";
        String normalizedName = element.name != null ? normalizeIdentifier(element.name.toLowerCase()) : "";
        
        // ID matching with normalization - 50% weight
        if (element.id != null && !element.id.isEmpty()) {
            double idScore = Math.max(
                calculateBestSimilarity(intentLower, element.id.toLowerCase()),
                calculateBestSimilarity(normalizedIntent, normalizedId)
            );
            score += idScore * 0.4;
        }
        
        // Name attribute matching with normalization - 30% weight
        if (element.name != null && !element.name.isEmpty()) {
            double nameScore = Math.max(
                calculateBestSimilarity(intentLower, element.name.toLowerCase()),
                calculateBestSimilarity(normalizedIntent, normalizedName)
            );
            score += nameScore * 0.4;
        }
        
        // Locator value extraction - 15% weight
        String locatorValue = extractValueFromLocator(element.locator);
        if (locatorValue != null && !locatorValue.isEmpty()) {
            String normalizedLocator = normalizeIdentifier(locatorValue.toLowerCase());
            double locatorScore = Math.max(
                calculateBestSimilarity(intentLower, locatorValue.toLowerCase()),
                calculateBestSimilarity(normalizedIntent, normalizedLocator)
            );
            score += locatorScore * 0.15;
        }
        
        // Text matching - 3% weight
        if (element.text != null && !element.text.isEmpty()) {
            score += calculateBestSimilarity(intentLower, element.text.toLowerCase()) * 0.03;
        }
        
        // Semantic attributes - 1.5% weight
        if (element.ariaLabel != null) {
            score += calculateBestSimilarity(intentLower, element.ariaLabel.toLowerCase()) * 0.015;
        }
        
        // Placeholder - 0.5% weight
        if (element.placeholder != null) {
            score += calculateBestSimilarity(intentLower, element.placeholder.toLowerCase()) * 0.005;
        }
        
        return score;
    }
    
    private String normalizeIdentifier(String identifier) {
        if (identifier == null) return "";
        
        // Convert camelCase/PascalCase to lowercase with separators
        // inpUserName -> inp_user_name -> inpusername
        return identifier
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .toLowerCase()
            .replaceAll("[^a-z0-9]", "");
    }
    
    private String extractValueFromLocator(String locator) {
        if (locator == null) return null;
        
        // Extract from @id='value'
        java.util.regex.Pattern idPattern = java.util.regex.Pattern.compile("@id='([^']*)'|@id=\"([^\"]*)\"");
        java.util.regex.Matcher idMatcher = idPattern.matcher(locator);
        if (idMatcher.find()) {
            return idMatcher.group(1) != null ? idMatcher.group(1) : idMatcher.group(2);
        }
        
        // Extract from @name='value'
        java.util.regex.Pattern namePattern = java.util.regex.Pattern.compile("@name='([^']*)'|@name=\"([^\"]*)\"");
        java.util.regex.Matcher nameMatcher = namePattern.matcher(locator);
        if (nameMatcher.find()) {
            return nameMatcher.group(1) != null ? nameMatcher.group(1) : nameMatcher.group(2);
        }
        
        // Extract from text() contains
        java.util.regex.Pattern textPattern = java.util.regex.Pattern.compile("text\\(\\)\\s*,\\s*'([^']*)'|text\\(\\)\\s*,\\s*\"([^\"]*)\"");
        java.util.regex.Matcher textMatcher = textPattern.matcher(locator);
        if (textMatcher.find()) {
            return textMatcher.group(1) != null ? textMatcher.group(1) : textMatcher.group(2);
        }
        
        return null;
    }
    
    private static class ElementInfo {
        String text, id, name, className, ariaLabel, placeholder, testId;
        String locator, description;
    }
    
    private static class ElementCandidate {
        String locator;
        double score;
        String description;
        
        ElementCandidate(String locator, double score, String description) {
            this.locator = locator;
            this.score = score;
            this.description = description;
        }
    }
}
package core.healing;

import com.intuit.karate.driver.Driver;
import core.platform.web.ChromeCustom;
import core.platform.utils.Logger;
import org.apache.commons.text.similarity.*;
import java.util.*;

public class SemanticLocator {
    private final IHealingDriver driver;
    private final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();
    private final JaccardSimilarity jaccard = new JaccardSimilarity();
    private static final HealingConfig config = HealingConfig.getInstance();

    public SemanticLocator(IHealingDriver driver) {
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
        if (ariaLocator != null)
            return ariaLocator;

        String roleLocator = findByRole(cleanIntent);
        if (roleLocator != null)
            return roleLocator;

        String testIdLocator = findByTestId(cleanIntent);
        if (testIdLocator != null)
            return testIdLocator;

        String semanticLocator = findBySemanticHtml(cleanIntent);
        if (semanticLocator != null)
            return semanticLocator;

        String fuzzyLocator = findByFuzzyText(cleanIntent);
        if (fuzzyLocator != null)
            return fuzzyLocator;

        return null;
    }

    private String extractLastKey(String intent) {
        if (intent == null || intent.isEmpty())
            return intent;

        // Handle dotted notation: login.inpUserName -> inpUserName
        if (intent.contains(".")) {
            String[] parts = intent.split("\\.");
            return parts[parts.length - 1];
        }

        return intent;
    }

    private String findByAria(String intent) {
        String[] ariaAttrs = { "aria-label", "aria-labelledby", "aria-describedby" };

        for (String attr : ariaAttrs) {
            String locator = String.format("//*[@%s='%s']", attr, intent);
            if (driver.exist(locator))
                return locator;

            locator = String.format("//*[contains(@%s, '%s')]", attr, intent);
            if (driver.exist(locator))
                return locator;
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
                if (driver.exist(locator))
                    return locator;
            }
        }
        return null;
    }

    private String findByTestId(String intent) {
        String[] testIdAttrs = { "data-testid", "data-test", "data-qa", "data-cy" };
        String normalized = intent.toLowerCase().replaceAll("\\s+", "-");

        for (String attr : testIdAttrs) {
            String locator = String.format("//*[@%s='%s']", attr, normalized);
            if (driver.exist(locator))
                return locator;
        }
        return null;
    }

    private String findBySemanticHtml(String intent) {
        String lower = intent.toLowerCase();

        if (lower.contains("button")) {
            String locator = String.format("//button[contains(text(), '%s')]", intent);
            if (driver.exist(locator))
                return locator;
        }

        if (lower.contains("link")) {
            String locator = String.format("//a[contains(text(), '%s')]", intent);
            if (driver.exist(locator))
                return locator;
        }

        if (lower.contains("input")) {
            String locator = String.format("//input[@placeholder='%s' or @name='%s']", intent, intent);
            if (driver.exist(locator))
                return locator;
        }

        return null;
    }


    private String findByFuzzyText(String intent) {
        Logger.debug("Attempting Fuzzy Semantic healing for intent: %s", intent);
        List<ElementCandidate> candidates = new ArrayList<>();

        // Get elements with coordinates and attributes using JS bridge
        List<ElementInfo> elements = getElementsWithDetailedInfo();
        List<Map<String, Object>> textMap = driver.visualTextMap();

        for (ElementInfo element : elements) {
            double score = calculateElementScore(element, intent);

            // Proximity Bonus: If this element is near a text label that matches the intent
            double proximityBonus = calculateProximityBonus(element, textMap, intent);
            score += proximityBonus;

            candidates.add(new ElementCandidate(element.locator, score, element.description));
        }

        // Sort by score and try top candidates
        candidates.sort((a, b) -> Double.compare(b.score, a.score));

        for (int i = 0; i < Math.min(3, candidates.size()); i++) {
            ElementCandidate candidate = candidates.get(i);
            if (candidate.score >= config.minSimilarityThreshold && driver.exist(candidate.locator)) {
                Logger.info("Success! Match found via Fuzzy/Proximity: %s (score: %.2f)", candidate.description,
                        candidate.score);
                return candidate.locator;
            }
        }

        return null;
    }

    private List<ElementInfo> getElementsWithDetailedInfo() {
        Logger.debug("Using JS bridge to extract interactive elements...");
        String js = "(function() {" +
                "  var results = [];" +
                "  var tags = ['button', 'a', 'input', 'select', 'textarea'];" +
                "  var elements = document.querySelectorAll(tags.join(','));" +
                "  for (var i = 0; i < elements.length; i++) {" +
                "    var el = elements[i];" +
                "    if (el.offsetWidth > 0 && el.offsetHeight > 0) {" +
                "      var rect = el.getBoundingClientRect();" +
                "      var attrs = {};" +
                "      for (var j = 0; j < el.attributes.length; j++) {" +
                "        attrs[el.attributes[j].name] = el.attributes[j].value;" +
                "      }" +
                "      results.push({" +
                "        tagName: el.tagName.toLowerCase()," +
                "        text: el.innerText || el.value || ''," +
                "        attributes: attrs," +
                "        x: rect.left, y: rect.top, w: rect.width, h: rect.height" +
                "      });" +
                "    }" +
                "  }" +
                "  return results;" +
                "})();";

        try {
            List<Map<String, Object>> raw = (List<Map<String, Object>>) driver.script(js);
            List<ElementInfo> results = new ArrayList<>();
            for (Map<String, Object> map : raw) {
                ElementInfo info = new ElementInfo();
                info.tagName = (String) map.get("tagName");
                info.text = (String) map.get("text");
                info.attributes = (Map<String, String>) map.get("attributes");
                info.x = ((Number) map.get("x")).intValue();
                info.y = ((Number) map.get("y")).intValue();
                info.width = ((Number) map.get("w")).intValue();
                info.height = ((Number) map.get("h")).intValue();
                info.locator = buildLocator(info);
                info.description = buildDescription(info);
                results.add(info);
            }
            return results;
        } catch (Exception e) {
            Logger.error("Failed to extract elements via JS: %s", e.getMessage());
            return Collections.emptyList();
        }
    }

    private double calculateProximityBonus(ElementInfo element, List<Map<String, Object>> textMap, String intent) {
        double maxBonus = 0;
        String normalizedIntent = normalizeIdentifier(intent);

        for (Map<String, Object> textLabel : textMap) {
            String labelText = (String) textLabel.get("text");
            if (labelText == null || labelText.length() < 2)
                continue;

            double similarity = calculateBestSimilarity(normalizedIntent, normalizeIdentifier(labelText));
            if (similarity > 0.7) {
                // Label is relevant. Check distance.
                int lx = ((Number) textLabel.get("x")).intValue();
                int ly = ((Number) textLabel.get("y")).intValue();

                // Distance from label to element center
                double dist = Math.sqrt(Math.pow(lx - (element.x + element.width / 2.0), 2) +
                        Math.pow(ly - (element.y + element.height / 2.0), 2));

                // Bonus decays with distance. Max 200px.
                if (dist < 200) {
                    double bonus = (1.0 - (dist / 200.0)) * 0.3 * similarity;
                    maxBonus = Math.max(maxBonus, bonus);
                }
            }
        }
        return maxBonus;
    }

    public ElementInfo extractElementInfo(String elementHtml) {
        ElementInfo info = new ElementInfo();

        // Extract tagName
        java.util.regex.Pattern tagPattern = java.util.regex.Pattern.compile("<([a-zA-Z0-9]+)");
        java.util.regex.Matcher tagMatcher = tagPattern.matcher(elementHtml);
        info.tagName = tagMatcher.find() ? tagMatcher.group(1).toLowerCase() : "*";

        // Extract text content
        java.util.regex.Pattern textPattern = java.util.regex.Pattern.compile(">([^<]+)<");
        java.util.regex.Matcher textMatcher = textPattern.matcher(elementHtml);
        if (textMatcher.find()) {
            info.text = textMatcher.group(1).trim();
        }

        // Extract attributes dynamically based on config
        for (HealingConfig.AttributeConfig attr : config.attributes) {
            String value = extractAttribute(elementHtml, attr.name);
            if (value != null) {
                info.attributes.put(attr.name, value);
                // Also map to legacy fields for backward compatibility in internal methods
                if ("name".equalsIgnoreCase(attr.name))
                    info.name = value;
            }
        }

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
        String tag = info.tagName != null ? info.tagName : "*";

        // Sort attributes by priority from config
        List<HealingConfig.AttributeConfig> sortedAttrs = new ArrayList<>(config.attributes);
        sortedAttrs.sort(Comparator.comparingInt(a -> a.priority));

        for (HealingConfig.AttributeConfig attr : sortedAttrs) {
            String value = info.attributes.get(attr.name);
            if (value != null && !value.isEmpty()) {
                if (isDynamic(attr, value))
                    continue; // Skip high-priority dynamic values if possible
                return String.format("//%s[@%s='%s']", tag, attr.name, value);
            }
        }

        // Fallback Priority: Combined Name and Text (Increased uniqueness)
        if (info.name != null && !info.name.isEmpty() && info.text != null && !info.text.isEmpty()) {
            return "//" + tag + "[@name='" + info.name + "' and contains(., '" + info.text + "')]";
        }

        // Absolute last resort: Any attribute even if dynamic
        for (HealingConfig.AttributeConfig attr : sortedAttrs) {
            String value = info.attributes.get(attr.name);
            if (value != null && !value.isEmpty()) {
                return String.format("//%s[@%s='%s']", tag, attr.name, value);
            }
        }

        return null;
    }

    private String buildDescription(ElementInfo info) {
        StringBuilder desc = new StringBuilder();
        if (info.text != null)
            desc.append("text:").append(info.text).append(" ");

        info.attributes.forEach((k, v) -> {
            desc.append(k).append(":").append(v).append(" ");
        });

        return desc.toString().trim();
    }

    private double calculateElementScore(ElementInfo element, String intent) {
        double score = 0;
        String intentLower = extractLastKey(intent).toLowerCase();
        String normalizedIntent = normalizeIdentifier(intentLower);

        for (HealingConfig.AttributeConfig attr : config.attributes) {
            String value = element.attributes.get(attr.name);
            if (value != null && !value.isEmpty()) {
                double weight = attr.weight;
                if (isDynamic(attr, value))
                    weight *= 0.1; // Penalize dynamic values

                double sim = Math.max(
                        calculateBestSimilarity(intentLower, value.toLowerCase()),
                        calculateBestSimilarity(normalizedIntent, normalizeIdentifier(value.toLowerCase())));
                score += sim * weight;
            }
        }

        // Text matching - Bonus weight
        if (element.text != null && !element.text.isEmpty()) {
            score += calculateBestSimilarity(intentLower, element.text.toLowerCase()) * 0.05;
        }

        return score;
    }

    private boolean isDynamic(HealingConfig.AttributeConfig attr, String value) {
        if (value == null || value.isEmpty())
            return false;
        if (attr.dynamicPatterns == null)
            return false;

        for (String pattern : attr.dynamicPatterns) {
            try {
                if (value.matches(pattern))
                    return true;
                if (value.toLowerCase().startsWith(pattern.toLowerCase().replace("^", "")))
                    return true;
            } catch (Exception e) {
                // Ignore invalid regex
            }
        }
        return false;
    }

    private String normalizeIdentifier(String identifier) {
        if (identifier == null)
            return "";

        // Convert camelCase/PascalCase to lowercase with separators
        // inpUserName -> inp_user_name -> inpusername
        return identifier
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }

    public static class ElementInfo {
        String tagName;
        String text, name; // name is still used in combined logic
        Map<String, String> attributes = new HashMap<>();
        String locator, description;
        int x, y, width, height;
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
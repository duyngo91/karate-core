package core.healing.domain;

import core.healing.application.port.CandidateExtractor;
import core.healing.domain.model.ElementNode;
import core.platform.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCandidateExtractor implements CandidateExtractor {

    @Override
    public ElementNode extract(String elementId, String locator) {
        ElementNode node = new ElementNode();
        node.setElementId(elementId);

        // 1. Enrich from ElementId (e.g. "login.username" -> name="username" or
        // text="username")
        String key = extractKey(elementId);
        node.addAttribute("name", key);
        node.addAttribute("id", key);
        node.addAttribute("data-testid", key);

        // 2. Parse Locator if possible (Robust segment-based extraction)
        if (locator != null && (locator.startsWith("//") || locator.startsWith("/"))) {
            try {
                // Split into segments and take the last one
                String[] segments = locator.split("/+");
                String lastSegment = "";
                for (int i = segments.length - 1; i >= 0; i--) {
                    if (!segments[i].trim().isEmpty()) {
                        lastSegment = segments[i].trim();
                        break;
                    }
                }

                if (!lastSegment.isEmpty()) {
                    // Handle axes like following-sibling::input
                    if (lastSegment.contains("::")) {
                        lastSegment = lastSegment.substring(lastSegment.indexOf("::") + 2);
                    }

                    // Extract tag
                    int bracketIdx = lastSegment.indexOf("[");
                    String tag = bracketIdx > 0 ? lastSegment.substring(0, bracketIdx) : lastSegment;
                    if (!tag.isEmpty() && tag.matches("[a-zA-Z0-9]+")) {
                        node.setTagName(tag);
                    }

                    // Extract all @attributes in this segment
                    java.util.regex.Pattern pAttr = java.util.regex.Pattern.compile("@([a-zA-Z0-9-]+)='([^']*)'");
                    java.util.regex.Matcher mAttr = pAttr.matcher(lastSegment);
                    while (mAttr.find()) {
                        node.addAttribute(mAttr.group(1), mAttr.group(2));
                    }

                    // Extract text() in this segment
                    java.util.regex.Pattern pText = java.util.regex.Pattern.compile("text\\(\\)\\s*=\\s*'([^']*)'");
                    java.util.regex.Matcher mText = pText.matcher(lastSegment);
                    if (mText.find()) {
                        node.setText(mText.group(1));
                    }

                    // If simple text match like [text()='foo'], this is also a label sign
                    if (node.getText() != null && node.getTagName().equals("button")) {
                        node.addAttribute("name", node.getText());
                    }
                }

                // Global attribute sweep (fallback)
                java.util.regex.Pattern pAll = java.util.regex.Pattern.compile("@([a-zA-Z0-9-]+)='([^']*)'");
                java.util.regex.Matcher mAll = pAll.matcher(locator);
                while (mAll.find()) {
                    if (!node.getAttributes().containsKey(mAll.group(1))) {
                        node.addAttribute(mAll.group(1), mAll.group(2));
                    }
                }

                // Neighbor Context
                if (locator.contains("following")) {
                    java.util.regex.Pattern pNeighbor = java.util.regex.Pattern.compile("\\[text\\(\\)\\s*=\\s*'([^']*)'\\]");
                    java.util.regex.Matcher mNeighbor = pNeighbor.matcher(locator);
                    if (mNeighbor.find()) {
                        node.setPrevSiblingText(mNeighbor.group(1));
                    }
                }


            } catch (Exception e) {
                // Ignore parsing errors
            }

            // ===== 3. Extract Structural Path Fingerprint =====
            try {
                List<String> structural = new ArrayList<>();

                // Match /tag or //tag
                Pattern p = Pattern.compile("(//?)([a-zA-Z0-9]+)");
                Matcher m = p.matcher(locator);

                while (m.find()) {
                    String axis = m.group(1); // "/" or "//"
                    String tag  = m.group(2).toLowerCase();

                    structural.add(tag);
                }

                if (!structural.isEmpty()) {
                    Collections.reverse(structural);
                    node.setStructuralPath(String.join(" > ", structural));
                }

            } catch (Exception e) {
                // ignore
            }

        }
        Logger.info("[TargetNode] ID: %s, Key: %s, Tag: %s, Attrs: %s", elementId, extractKey(elementId), node.getTagName(), node.getAttributes());
        return node;
    }

    private String extractKey(String elementId) {
        if (elementId == null) return "";
        int dotIdx = elementId.lastIndexOf('.');
        return dotIdx >= 0 ? elementId.substring(dotIdx + 1) : elementId;
    }



}


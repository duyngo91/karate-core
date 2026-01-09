package core.healing.engine;

import core.healing.IHealingDriver;
import core.healing.model.ElementNode;
import core.platform.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CandidateFinder {

    public static List<ElementNode> findCandidates(IHealingDriver driver) {
        Logger.debug("Using JS bridge to extract interactive elements...");

        // Extended tag list for better coverage
        String js = "(function() {" +
                "  var results = [];" +
                "  var tags = ['button', 'a', 'input', 'select', 'textarea', 'label', 'span', 'div', 'p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'img', 'li', 'td'];"
                +
                "  var elements = document.querySelectorAll(tags.join(','));" +
                "  for (var i = 0; i < elements.length; i++) {" +
                "    var el = elements[i];" +
                "    if (el.offsetWidth > 0 && el.offsetHeight > 0) {" +
                "      var rect = el.getBoundingClientRect();" +
                "      var attrs = {};" +
                "      for (var j = 0; j < el.attributes.length; j++) {" +
                "        attrs[el.attributes[j].name] = el.attributes[j].value;" +
                "      }" +
                "      " +
                "      var depth = 0;" +
                "      var parent = el.parentElement;" +
                "      while(parent) { depth++; parent = parent.parentElement; }" +
                "      " +
                "      var parentTag = el.parentElement ? el.parentElement.tagName.toLowerCase() : null;" +
                "      " +
                "      results.push({" +
                "        tagName: el.tagName.toLowerCase()," +
                "        text: el.innerText || el.value || ''," +
                "        attributes: attrs," +
                "        x: rect.left, y: rect.top, w: rect.width, h: rect.height," +
                "        depth: depth," +
                "        parentTag: parentTag" +
                "      });" +
                "    }" +
                "  }" +
                "  return results;" +
                "})();";

        try {
            Object result = driver.script(js);
            if (result instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> raw = (List<Map<String, Object>>) result;
                List<ElementNode> nodes = new ArrayList<>();

                for (Map<String, Object> map : raw) {
                    ElementNode node = new ElementNode();
                    node.setTagName((String) map.get("tagName"));
                    node.setText((String) map.get("text"));

                    @SuppressWarnings("unchecked")
                    Map<String, String> attrs = (Map<String, String>) map.get("attributes");
                    node.setAttributes(attrs);

                    if (map.get("x") instanceof Number)
                        node.setX(((Number) map.get("x")).intValue());
                    if (map.get("y") instanceof Number)
                        node.setY(((Number) map.get("y")).intValue());
                    if (map.get("w") instanceof Number)
                        node.setWidth(((Number) map.get("w")).intValue());
                    if (map.get("h") instanceof Number)
                        node.setHeight(((Number) map.get("h")).intValue());

                    if (map.get("depth") instanceof Number)
                        node.setDepth(((Number) map.get("depth")).intValue());
                    node.setParentTag((String) map.get("parentTag"));
                    node.setPrevSiblingTag((String) map.get("prevSiblingTag"));
                    node.setPrevSiblingText((String) map.get("prevSiblingText"));

                    nodes.add(node);
                }
                return nodes;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            Logger.error("Failed to extract elements via JS: %s", e.getMessage());
            return Collections.emptyList();
        }
    }
}

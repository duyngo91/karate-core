package core.healing.infrastructure.golden.extractor;

import core.healing.application.port.IHealingDriver;
import core.healing.domain.model.ElementMetadata;

import java.util.HashMap;
import java.util.Map;

public class JsDomSnapshotExtractor implements DomSnapshotExtractor {

    @Override
    public ElementMetadata extract(IHealingDriver driver, String elementId, String locator) {
        ElementMetadata metadata = null;
        System.err.println("DEBUG: Attempting to capture Golden State for " + elementId);
        String js =
                """
                (function() {
                  var el = null;
                
                  if (arguments[0].startsWith('/')) {
                    el = document
                      .evaluate(arguments[0], document, null,
                        XPathResult.FIRST_ORDERED_NODE_TYPE, null)
                      .singleNodeValue;
                  } else {
                    el = document.querySelector(arguments[0]);
                  }
                
                  if (!el) return null;
                
                  var attrs = {};
                  for (var i = 0; i < el.attributes.length; i++) {
                    attrs[el.attributes[i].name] = el.attributes[i].value;
                  }
                
                  var rect = el.getBoundingClientRect();
                
                  function getStructuralPath(el) {
                    var path = [];
                    var cur = el;
                    var limit = 10;
                    while (cur && limit-- > 0) {
                      path.push(cur.tagName.toLowerCase());
                      cur = cur.parentElement;
                    }
                    return path.join(' > ');
                  }
                
                  var prev = el.previousElementSibling;
                
                  return {
                    tagName: el.tagName.toLowerCase(),
                    text: el.textContent
                            ? el.textContent.substring(0, 200).trim()
                            : '',
                    attributes: attrs,
                    neighborText: prev && prev.textContent
                            ? prev.textContent.trim()
                            : '',
                    structuralPath: getStructuralPath(el),
                    x: rect.left,
                    y: rect.top,
                    w: rect.width,
                    h: rect.height
                  };
                })();
                
                """;

        String finalJs = js.replace("arguments[0]", "'" + locator.replace("'", "\\'") + "'");

        Object result = driver.script(finalJs);
        if (result != null) {
            System.err.println("DEBUG: Script result type: " + result.getClass().getName());
        } else {
            System.err.println("DEBUG: Script result is NULL");
        }

        if (result instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) result;

            metadata = new ElementMetadata();
            metadata.setElementId(elementId);
            metadata.setLocator(locator);
            metadata.setTagName((String) data.getOrDefault("tagName", ""));
            metadata.setText((String) data.getOrDefault("text", ""));
            metadata.setNeighborText((String) data.getOrDefault("neighborText", ""));
            metadata.setStructuralPath((String) data.getOrDefault("structuralPath", ""));

            if (data.containsKey("attributes") && data.get("attributes") instanceof Map) {
                Map<String, String> attrs = new HashMap<>();
                Map<String, Object> rawAttrs = (Map<String, Object>) data.get("attributes");
                for (Map.Entry<String, Object> entry : rawAttrs.entrySet()) {
                    attrs.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                metadata.setAttributes(attrs);
            }

            if (data.get("x") instanceof Number)
                metadata.setX(((Number) data.get("x")).intValue());
            if (data.get("y") instanceof Number)
                metadata.setY(((Number) data.get("y")).intValue());
            if (data.get("w") instanceof Number)
                metadata.setWidth(((Number) data.get("w")).intValue());
            if (data.get("h") instanceof Number)
                metadata.setHeight(((Number) data.get("h")).intValue());
        }
        return metadata;
    }
}

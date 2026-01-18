package core.healing.application.candidate;

import core.healing.application.port.IHealingDriver;
import core.healing.domain.model.ElementNode;
import core.platform.utils.Logger;

import java.util.*;

/**
 * Application-level candidate collector using JS bridge.
 */
public class JsCandidateCollector {

    private final IHealingDriver driver;

    public JsCandidateCollector(IHealingDriver driver) {
        this.driver = driver;
    }

    public List<ElementNode> collect() {
        Logger.debug("[Healing] Collecting candidates via JS");
        String js = """
        (function() {
          function getDomIndex(el) {
            if (!el || !el.parentElement) return -1;
            return Array.from(el.parentElement.children).indexOf(el);
          };
          function findFormContainer(el) {
            let cur = el;
            while (cur) {
              if (
                cur.tagName === 'FORM' ||
                cur.getAttribute('role') === 'form' ||
                cur.className?.toLowerCase().includes('form') ||
                cur.className?.toLowerCase().includes('login')
              ) {
                return cur;
              }
              cur = cur.parentElement;
            }
            return null;
          };
          function getStructuralPath(el) {
            var path = [];
            var cur = el;
            var limit = 10;
            while (cur && limit-- > 0) {
              path.push(cur.tagName.toLowerCase());
              cur = cur.parentElement;
            }
            return path.join(' > ');
          };
          
          var results = [];
          var tags = ['button', 'a', 'input', 'select', 'textarea', 'label', 'span', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'li'];
          var elements = document.querySelectorAll(tags.join(','));
          for (var i = 0; i < elements.length; i++) {
            var el = elements[i];
            if (el.offsetWidth > 0 && el.offsetHeight > 0) {
              var rect = el.getBoundingClientRect();
              var attrs = {};
              for (var j = 0; j < el.attributes.length; j++) {
                attrs[el.attributes[j].name] = el.attributes[j].value;
              }
              
              var depth = 0;
              var parent = el.parentElement;
              while(parent) { depth++; parent = parent.parentElement; }
              
              var parentTag = el.parentElement ? el.parentElement.tagName.toLowerCase() : null;
              var container = findFormContainer(el);
              results.push({
                tagName: el.tagName.toLowerCase(),
                text: el.innerText || el.value || '',
                attributes: attrs,
                x: rect.left, y: rect.top, w: rect.width, h: rect.height,
                depth: depth,
                domIndex: getDomIndex(el),
                formId: container ? container.id || container.className || container.tagName : null,
                parentTag: parentTag,
                structuralPath: getStructuralPath(el)
              });
            }
          }
          return results;
        })();
        """;

        try {
            Object result = driver.script(js);
            if (!(result instanceof List<?> raw)) {
                return Collections.emptyList();
            }

            List<ElementNode> nodes = new ArrayList<>();
            for (Object o : raw) {
                if (!(o instanceof Map<?, ?> map)) continue;
                nodes.add(mapToNode((Map<String, Object>) map));
            }
            return nodes;

        } catch (Exception e) {
            Logger.error("Candidate collection failed: %s", e.getMessage());
            return Collections.emptyList();
        }
    }

    private ElementNode mapToNode(Map<String, Object> map) {
        ElementNode node = new ElementNode();
        node.setTagName((String) map.get("tagName"));
        node.setText((String) map.get("text"));
        node.setAttributes((Map<String, String>) map.get("attributes"));

        node.setX(toInt(map.get("x")));
        node.setY(toInt(map.get("y")));
        node.setWidth(toInt(map.get("w")));
        node.setHeight(toInt(map.get("h")));
        node.setDepth(toInt(map.get("depth")));

        node.setDomIndex((Integer) map.get("domIndex"));
        node.setFormId((String) map.get("formId"));
        node.setParentTag((String) map.get("parentTag"));
        node.setStructuralPath((String) map.get("structuralPath"));

        return node;
    }

    private int toInt(Object o) {
        return o instanceof Number n ? n.intValue() : 0;
    }
}

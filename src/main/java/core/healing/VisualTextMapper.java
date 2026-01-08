package core.healing;

import com.intuit.karate.driver.Driver;
import core.platform.web.ChromeCustom;
import core.platform.utils.Logger;
import java.util.*;

public class VisualTextMapper {
    private final IHealingDriver driver;

    public VisualTextMapper(IHealingDriver driver) {
        this.driver = driver;
    }

    public List<Map<String, Object>> getVisualTextMap() {
        Logger.debug("Extracting visual text map from the current page...");

        String js = "(function() {" +
                "  var results = [];" +
                "  var elements = document.querySelectorAll('*:not(script):not(style)');" +
                "  for (var i = 0; i < elements.length; i++) {" +
                "    var el = elements[i];" +
                "    var text = '';" +
                "    for (var j = 0; j < el.childNodes.length; j++) {" +
                "      if (el.childNodes[j].nodeType === 3) {" +
                "        text += el.childNodes[j].textContent.trim();" +
                "      }" +
                "    }" +
                "    if (text.length > 0 && el.offsetWidth > 0 && el.offsetHeight > 0) {" +
                "      var rect = el.getBoundingClientRect();" +
                "      results.push({" +
                "        text: text," +
                "        x: Math.round(rect.left)," +
                "        y: Math.round(rect.top)," +
                "        width: Math.round(rect.width)," +
                "        height: Math.round(rect.height)" +
                "      });" +
                "    }" +
                "  }" +
                "  return results;" +
                "})();";

        try {
            Object result = driver.script(js);
            if (result instanceof List) {
                return (List<Map<String, Object>>) result;
            }
        } catch (Exception e) {
            Logger.error("Failed to extract visual text map: %s", e.getMessage());
        }
        return Collections.emptyList();
    }
}

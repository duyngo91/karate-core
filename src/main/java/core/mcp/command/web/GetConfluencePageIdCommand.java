package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class GetConfluencePageIdCommand extends AbstractDriverCommand {
    public GetConfluencePageIdCommand(ChromeCustom driver) {
        super(driver, null);
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String script = 
            "(function() {" +
            "  var meta = document.querySelector('meta[name=\"ajs-page-id\"]');" +
            "  if (meta) return meta.getAttribute('content');" +
            "  var url = window.location.href;" +
            "  var match = url.match(/pageId=(\\d+)/);" +
            "  if (match) return match[1];" +
            "  match = url.match(/pages\\/(\\d+)/);" +
            "  if (match) return match[1];" +
            "  return null;" +
            "})();";
        
        return getDriver().script(script);
    }

    


}

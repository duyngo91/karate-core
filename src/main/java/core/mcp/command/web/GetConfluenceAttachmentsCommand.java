package core.mcp.command.web;

import core.mcp.command.AbstractDriverCommand;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.web.ChromeCustom;

import java.util.Map;

public class GetConfluenceAttachmentsCommand extends AbstractDriverCommand {

    public GetConfluenceAttachmentsCommand(ChromeCustom driver) {
        super(driver, args -> ArgumentValidator.requireNonNull(args, ToolNames.PAGE_ID, ToolNames.BASE_URL));
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String pageId = args.get(ToolNames.PAGE_ID).toString();
        String baseUrl = args.get(ToolNames.BASE_URL).toString();
        
        String apiUrl = baseUrl + "/rest/api/content/" + pageId + "/child/attachment";
        String script = String.format(
            "(function() {" +
            "  var xhr = new XMLHttpRequest();" +
            "  xhr.open('GET', '%s', false);" +
            "  xhr.withCredentials = true;" +
            "  xhr.send(null);" +
            "  if (xhr.status !== 200) {" +
            "    return {error: 'HTTP ' + xhr.status};" +
            "  }" +
            "  return JSON.parse(xhr.responseText);" +
            "})();",
            apiUrl
        );
        
        return getChrome().script(script);
    }



}

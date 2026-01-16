package core.mcp.command.mobile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.driver.Driver;
import com.intuit.karate.http.HttpClientFactory;
import core.mcp.command.AbstractDriverCommand;
import core.mcp.config.McpConfig;
import core.mcp.constant.ToolNames;
import core.mcp.validation.ArgumentValidator;
import core.platform.manager.DriverManager;
import core.platform.mobile.AndroidCustom;

import java.lang.reflect.Type;
import java.util.Map;

public class ConnectAndroidCommand extends AbstractDriverCommand {
    public ConnectAndroidCommand() {
        super(null, args -> ArgumentValidator.requireNonEmpty(args, ToolNames.INFO));
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String capabilities = (String) args.get(ToolNames.INFO);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = gson.fromJson(capabilities, type);
        FeatureRuntime featureRuntime = FeatureRuntime.forTempUse(HttpClientFactory.DEFAULT);
        ScenarioRuntime scenarioRuntime = featureRuntime.scenarios.next();
        String session = args.getOrDefault(ToolNames.SESSION, McpConfig.getInstance().getDefaultSession()).toString();
        if (!DriverManager.hasDriver(session)) {
            DriverManager.addDriver(session, AndroidCustom.start(map, scenarioRuntime));
        }
        return "Android connected";
    }

}

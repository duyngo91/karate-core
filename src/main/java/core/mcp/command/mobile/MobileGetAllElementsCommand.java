package core.mcp.command.mobile;

import com.intuit.karate.driver.Driver;
import core.mcp.command.AbstractDriverCommand;

import java.util.Map;

public class MobileGetAllElementsCommand extends AbstractDriverCommand {
    public MobileGetAllElementsCommand(Driver driver) {
        super(driver, null);
    }

    @Override
    public Object execute(Map<String, Object> args) {
        return getMobile().getAllElementsInfo();
    }

    @Override
    public boolean isRecordable() {
        return false;
    }

}

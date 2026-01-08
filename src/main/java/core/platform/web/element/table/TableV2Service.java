package core.platform.web.element.table;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;

@TablePriority(1)
public class TableV2Service implements TableService {
    @Override
    public String getType() {
        return "v2";
    }

    @Override
    public int getPriority() {
        return TableService.super.getPriority();
    }

    @Override
    public Element createTable(Driver driver, String xpathHeader) {
        return new TableV2(driver, xpathHeader);
    }

    @Override
    public Element createTable(Driver driver, String xpathHeader, String xpathRow) {
        return new TableV2(driver, xpathHeader, xpathRow);
    }

}

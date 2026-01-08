package core.platform.web.element.table;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;

@TablePriority(1)
public class DefaultTableService implements TableService {
    @Override
    public String getType() {
        return "defaultTable";
    }

    @Override
    public int getPriority() {
        return TableService.super.getPriority();
    }

    @Override
    public Element createTable(Driver driver, String xpathHeader) {
        return new Table(driver, xpathHeader);
    }

    @Override
    public Element createTable(Driver driver, String xpathHeader, String xpathRow) {
        return new Table(driver, xpathHeader, xpathRow);
    }

}

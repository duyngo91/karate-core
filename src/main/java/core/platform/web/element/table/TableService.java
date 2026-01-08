package core.platform.web.element.table;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;

public interface TableService {
    String getType();
    default int getPriority() {
        TablePriority priority = getClass().getAnnotation(TablePriority.class);
        return priority != null ? priority.value() : 0;
    }

    Element createTable(Driver driver, String xpathHeader);
    Element createTable(Driver driver, String xpathHeader, String xpathRow);
}
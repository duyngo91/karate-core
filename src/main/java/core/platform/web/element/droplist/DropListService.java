package core.platform.web.element.droplist;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;


public interface DropListService {
    String getType();
    default int getPriority() {
        DropListPriority priority = getClass().getAnnotation(DropListPriority.class);
        return priority != null ? priority.value() : 0;
    }

    Element createDropList(Driver driver, String locator);
    Element createDropList(Driver driver, String locator, String items);
    Element createDropList(Driver driver, String locator, String searchField, String items);
}

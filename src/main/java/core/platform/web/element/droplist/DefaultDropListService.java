package core.platform.web.element.droplist;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;

@DropListPriority(1)
public class DefaultDropListService implements DropListService {
    @Override
    public String getType() {
        return "defaultDropList";
    }

    @Override
    public Element createDropList(Driver driver, String locator) {
        return new DropList(driver, locator);
    }

    @Override
    public Element createDropList(Driver driver, String locator, String items) {
        return new DropList(driver, locator, items);
    }

    @Override
    public Element createDropList(Driver driver, String locator, String searchField, String items) {
        return new DropList(driver, locator, searchField, items);
    }
}

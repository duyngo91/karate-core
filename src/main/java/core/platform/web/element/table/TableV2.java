package core.platform.web.element.table;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;

public class TableV2 extends Table{

    private static final String XPATH_CELL = "//div[@role='gridcell']";

    public TableV2(Driver driver, String xpathHeader) {
        super(driver, xpathHeader);
        setXpathCell(XPATH_CELL);
    }

    public TableV2(Driver driver, String xpathHeader, String xpathRow) {
        super(driver, xpathHeader, xpathRow);
        setXpathCell(XPATH_CELL);
    }

    public TableV2(Driver driver, Element element) {
        super(driver, element);
        setXpathCell(XPATH_CELL);
    }
}

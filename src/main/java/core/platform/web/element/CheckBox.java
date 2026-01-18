package core.platform.web.element;

import com.intuit.karate.driver.Driver;

import java.util.List;
import java.util.stream.Collectors;

public class CheckBox extends WebElement{

    private String locator;
    public CheckBox(Driver driver, String locator) {
        super(driver, locator);
    }


    public List<CheckBox> getCheckBoxs(){
        return driver.locateAll(getLocator())
                .stream().map(x -> new CheckBox(driver, x.getLocator()))
                .collect(Collectors.toList());
    }

    public boolean isChecked() {
        return (boolean) driver.script(getLocator(), "_.checked");
    }

    public List<Boolean> isCheckedAll() {
        return getCheckBoxs().stream().map(CheckBox::isChecked).collect(Collectors.toList());
    }

    public CheckBox set(Boolean expected) {
        if(expected != null){
            driver.waitFor(getLocator());
            if(isChecked() != expected) click();
        }

        return this;
    }

    public CheckBox setAll(Boolean expected) {
        if(expected != null){
            driver.waitFor(getLocator());
            getCheckBoxs().stream().forEach(e -> {
                if(e.isChecked() != expected) e.click();
            });
        }
        return this;
    }

}

package core.platform.web.element.droplist;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.DriverElement;
import com.intuit.karate.driver.Element;
import com.intuit.karate.driver.Keys;
import core.platform.web.ChromeCustom;
import core.platform.web.element.WebElement;
import core.platform.web.wait.Wait;

import java.util.*;
import java.util.stream.Collectors;

public class DropList extends WebElement  {

    private static final String DEFAULT_ITEMS = "//*[@role='option' and .//span] | //*[@role='option' and .//div] | //nz-option-item";
    private static final String SPAN = "//span";
    private static final String INPUT = "//input";
    private static final String LAST_ITEM_SUFFIX = "[last()]";
    private static final int SHORT_TIME = 500;
    private String locatorItems;
    private String searchField;
    private String buttonClear;
    private String xpathDropListValue;
    private String selectAllCheckbox;

    public DropList(Driver driver, String dropList) {
        super(driver, DriverElement.locatorExists(driver, dropList));
    }

    public DropList(Driver driver, String dropList, String items) {
        super(driver, DriverElement.locatorExists(driver, dropList));
        this.locatorItems = items;
    }

    public DropList(Driver driver, String dropList, String searchField, String items) {
        super(driver, DriverElement.locatorExists(driver, dropList));
        this.searchField = searchField;
        this.locatorItems = items;
    }

    @Override
    public Element waitFor() {
        getElement().waitFor();
        return this;
    }

    private String getLocatorItems(){
        if(locatorItems == null){
            return DEFAULT_ITEMS;
        }else{
            return locatorItems;
        }
    }

    public List<String> getExactlyItemText(){
        return getExactlyItem().stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }


    public List<String> getItemText(){
        return getItems().stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }

    public List<String> getItemTextByScroll(String xpathOfScroll, int y){
        List<String> rs = getItemText();

        while(!getDriver().isScrollToEndOfBottom(xpathOfScroll)){
            getDriver().scrollElementBy(xpathOfScroll, 0, y);
            delay(500);
            List<String> current = getItemText();
            rs.addAll(current);
            rs = rs.stream().distinct().collect(Collectors.toList());
        }

        return rs;
    }


    public List<Element> getExactlyItem(){
        List<WebElement> list = driver.locateAll(getLocatorItems()).stream().map(e -> new WebElement(driver, e)).collect(Collectors.toList());
        if(list.isEmpty()) return new ArrayList<>();
        return list.stream().filter(e -> ((ChromeCustom) driver).isVisible(e.getLocator())).collect(Collectors.toList());
    }

    public void waitForAtLeastOneItemAppear(){
        Wait.until(10000, 1000, ()-> !getExactlyItem().isEmpty());
    }

    public List<Element> getItems(){
        driver.waitFor(getLocatorItems());
        return driver.locateAll(getLocatorItems());
    }

    public Optional<Element> getItemHasText(String text){
        return getItems().stream().filter(i -> i.getText().trim().equalsIgnoreCase(text.trim())).findFirst();
    }

    public Optional<Element> getItemContainsText(String text){
        return getItems().stream().filter(i -> i.getText().trim().contains(text.trim())).findFirst();
    }

    public WebElement setButtonClear(String buttonClear) {
        this.buttonClear = buttonClear;
        return this;
    }

    public WebElement setXpathDropListValue(String xpathDropListValue) {
        this.xpathDropListValue = xpathDropListValue;
        return this;
    }

    public WebElement setSelectAll(String selectAllXpath) {
        this.selectAllCheckbox = selectAllXpath;
        return this;
    }

    @Override
    public WebElement click(){
        super.click();
        return this;
    }

    public WebElement selectAll() {
        driver.waitFor(selectAllCheckbox).click();
        return this;
    }

    public WebElement mouseClick(){
        getElement().mouse().click();
        return this;
    }

    public DropList search(String value){
        driver.waitFor(searchField).clear().input(value);
        return this;
    }

    public WebElement searchAndClick(String itemValue) {
        search(itemValue);
        delay(2 * SHORT_TIME);
        clickTextContains(itemValue);
        return this;
    }

    public WebElement searchAndClick(String searchValue, String itemValue) {
        search(searchValue);
        delay(SHORT_TIME);
        clickTextContains(itemValue);
        return this;
    }

    public DropList clickText(String value){
        Optional<Element> item = getItemHasText(value);
        item.ifPresent(Element::click);
        return this;
    }

    public DropList clickTextContains(String value){
        Optional<Element> item = getItemContainsText(value);
        item.ifPresent(Element::click);
        return this;
    }

    public DropList enter(){
        input(String.valueOf(Keys.ENTER));
        return this;
    }

    public DropList open(){
        delay(SHORT_TIME);
        click();
        delay(SHORT_TIME);
        if(isNotDisplayItem()){
            mouseClick();
            delay(SHORT_TIME);
            if(isNotDisplayItem()){
                click();
                delay(SHORT_TIME);
                if(isNotDisplayItem()){
                    enter();
                }
            }
        }
        return this;
    }

    public WebElement selectText(List<String> values){
        if(!values.isEmpty()){
            scroll();
            open();
            values.forEach(this::clickText);
        }
        return this;
    }

    public WebElement selectTextContains(List<String> values){
        if(!values.isEmpty()){
            scroll();
            open();
            values.forEach(this::clickTextContains);
        }
        return this;
    }

    public WebElement selectText(String value){
        if(value != null && !value.isEmpty()){
            scroll();
            open();
            clickText(value);
        }
        return this;
    }

    public WebElement selectTextContains(String value){
        if(value != null && !value.isEmpty()){
            scroll();
            open();
            clickTextContains(value);
        }
        return this;
    }

    public String getCurrentDropListValue() {
        return driver.waitFor(xpathDropListValue).getText();
    }

    public WebElement selectTextAfterSearch(String itemValue) {
        return selectTextAfterSearch(itemValue,itemValue);
    }

    public WebElement selectTextAfterSearch(String itemValue, String searchValue) {
        if (itemValue != null && searchValue != null) {
            scroll();
            open();
            searchAndClick(searchValue,itemValue);
        }
        return this;
    }

    public WebElement selectListOptionsAfterSearch(List<String> listValue) {
        if (listValue != null && !listValue.isEmpty()) {
            scroll();
            open();
            listValue.forEach(this::searchAndClick);
        }
        return this;
    }

    public WebElement clearOption() {
        scroll();
        delay(SHORT_TIME);
        driver.waitFor(buttonClear).delay(2 * SHORT_TIME).mouse().click();
        return this;
    }

    public List<String> selected(){
        List<Element> spans = locateAll(getLocator() + SPAN);
        List<Element> inputs = locateAll(getLocator() + INPUT);
        List<String> result = new ArrayList<>();
        result.addAll(spans.stream().map( s -> s.getText().trim()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        result.addAll(inputs.stream().map( s -> s.getValue().trim()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        result.add(((ChromeCustom)driver).textContent(getLocator()).trim());

        return result.stream().distinct().collect(Collectors.toList());
    }

    public boolean isNotDisplayItem(){
        return getExactlyItem().isEmpty();
    }

    public ChromeCustom getDriver(){
        return (ChromeCustom) driver;
    }

    public Set<String> getAllOptionValues() {
        scroll();
        delay(SHORT_TIME);
        open();
        delay(SHORT_TIME);
        var listOptions = new LinkedHashSet<String>();
        int numberOfOptions;
        do {
            numberOfOptions = listOptions.size();
            getExactlyItem().forEach(elm ->
                    listOptions.add(elm.getText().trim().toString()));
            driver.scroll(getLocatorItems() + LAST_ITEM_SUFFIX).delay(SHORT_TIME);
        }while (numberOfOptions != listOptions.size());
        return listOptions;
    }
}

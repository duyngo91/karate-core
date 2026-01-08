package core.platform.web.element;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.karate.Json;
import com.intuit.karate.core.AutoDef;
import com.intuit.karate.driver.DevToolsDriver;
import com.intuit.karate.driver.DevToolsMessage;
import com.intuit.karate.driver.Driver;
import core.platform.web.exceptions.RunException;
import core.platform.web.wait.Wait;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Tab {
    private final Driver driver;

    public Tab(Driver driver) {
        this.driver = driver;
    }

    @AutoDef
    public Object getTabs() {
        return Json.of(getPage());
    }

    @AutoDef
    public JSONArray getTargets() {
        DevToolsMessage msg = getDriver().method("Target.getTargets").send();
        Json rs = Json.of(msg.getResult().getValue());
        return rs.get("targetInfos");
    }

    @AutoDef
    public void switchToTitle(String title) {
        Optional<TabInfo> tab = waitTitle(10000, 1000, title);
        tab.ifPresent(jsonObject -> getDriver().switchPage(jsonObject.getTitle()));
    }


    @AutoDef
    public void switchToTitleContains(String title) {
        Optional<TabInfo> tab = waitTitleContains(10000, 1000, title);
        tab.ifPresent(jsonObject -> getDriver().switchPage(jsonObject.getTitle()));
    }


    @AutoDef
    public Optional<TabInfo> waitTitle(int timeOutMilliSeconds, int retryTimeMilliSeconds, String title) {
        Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> isExistTitle(title));
        return getPage().stream().filter(filterTitle(title)).findFirst();
    }

    @AutoDef
    public Optional<TabInfo> waitTitle(String title) {
        return waitTitle(5000, 1000, title);
    }

    @AutoDef
    public Optional<TabInfo> waitTitleContains(int timeOutMilliSeconds, int retryTimeMilliSeconds, String title) {
        Wait.until(timeOutMilliSeconds, retryTimeMilliSeconds, () -> isExistTitleContains(title));
        return getPage().stream().filter(filterTitleContains(title)).findFirst();

    }

    @AutoDef
    public boolean isExistTitle(String title) {
        return getPage().stream().anyMatch(filterTitle(title));
    }

    @AutoDef
    public boolean isExistTitleContains(String title) {
        return getPage().stream().anyMatch(filterTitleContains(title));
    }


    @AutoDef
    public void openNewTab(String url) {
        getDriver().script("window.open('" + url + "', '_blank')");
    }


    @AutoDef
    public void closeByTitle(String title) {
        String currentTitle = getDriver().getTitle();
        Optional<TabInfo> tab = getPage().stream().filter(filterTitle(title)).findFirst();
        tab.ifPresent(t -> closeAndSwitchTab(currentTitle, t));
    }

    @AutoDef
    public void closeByTitleContains(String title) {
        String currentTitle = getDriver().getTitle();
        Optional<TabInfo> tab = getPage().stream().filter(filterTitleContains(title)).findFirst();
        tab.ifPresent(t -> closeAndSwitchTab(currentTitle, t));
    }

    private List<TabInfo> getPage() {
        return getTargetAsCls().stream().filter(t -> t.getType().equalsIgnoreCase("page"))
                .collect(Collectors.toList());
    }


    private void closeAndSwitchTab(String currentTitle, TabInfo tab) {
        close(tab);
        switchToCurrentTab(currentTitle, tab);
    }

    private void switchToCurrentTab(String currentTitle, TabInfo tab) {
        if (tab.getTitle().equalsIgnoreCase(currentTitle)) {
            getDriver().switchPage(
                    getPage().stream().filter(skipTitle(tab.getTitle())).findFirst().orElseThrow().getTitle()
            );
        } else {
            getDriver().switchPage(currentTitle);
        }
    }

    private void close(TabInfo tab) {
        getDriver().switchPage(tab.getTitle());
        getDriver().close();
    }
    public DevToolsDriver getDriver() {
        return (DevToolsDriver) driver;
    }

    private Predicate<TabInfo> filterTitleContains(String title) {
        return tab -> tab.getTitle().toLowerCase().contains(title.toLowerCase().trim());
    }

    private Predicate<TabInfo> filterTitle(String title) {
        return tab -> tab.getTitle().equalsIgnoreCase(title.trim());
    }
    private Predicate<TabInfo> skipTitle(String title) {
        return tab -> !tab.getTitle().equalsIgnoreCase(title.trim());
    }


    private List<TabInfo> getTargetAsCls() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(getTargets().toJSONString(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RunException(e.getMessage());
        }
    }

}

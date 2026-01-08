package core.healing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;

public class LocatorRepository {
    private static LocatorRepository instance;
    private Map<String, Map<String, String>> locators = new HashMap<>();
    private final Gson gson = new Gson();
    
    private LocatorRepository() {}
    
    public static LocatorRepository getInstance() {
        if (instance == null) {
            instance = new LocatorRepository();
        }
        return instance;
    }
    
    public void loadFromFile(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Map<String, Map<String, String>> loaded = gson.fromJson(reader,
                new TypeToken<Map<String, Map<String, String>>>(){}.getType());
            if (loaded != null) {
                locators.putAll(loaded);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load locators: " + e.getMessage());
        }
    }
    
    public void loadFromJson(String json) {
        locators = gson.fromJson(json, 
            new TypeToken<Map<String, Map<String, String>>>(){}.getType());
    }
    
    public String getLocator(String page, String element) {
        Map<String, String> pageLocators = locators.get(page);
        if (pageLocators == null) {
            throw new RuntimeException("Page not found: " + page);
        }
        
        String locator = pageLocators.get(element);
        if (locator == null) {
            throw new RuntimeException("Element not found: " + element + " in page: " + page);
        }
        
        return locator;
    }
    
    public void updateLocator(String page, String element, String newLocator) {
        locators.computeIfAbsent(page, k -> new HashMap<>()).put(element, newLocator);
    }
    
    public void saveToFile(String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(locators, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save locators: " + e.getMessage());
        }
    }
    
    public String getElementId(String page, String element) {
        return page + "." + element;
    }
    
    public Map<String, Map<String, String>> getAllLocators() {
        return locators;
    }
}

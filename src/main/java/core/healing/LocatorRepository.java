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
    
    public Map<String, Map<String, String>> getAllLocators() {
        return locators;
    }
}

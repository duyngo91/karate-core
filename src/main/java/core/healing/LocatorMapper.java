package core.healing;

import java.util.*;

public class LocatorMapper {
    private static LocatorMapper instance;
    private final Map<String, String> locatorToId = new HashMap<>();
    private final LocatorRepository repository;
    
    private LocatorMapper() {
        this.repository = LocatorRepository.getInstance();
    }
    
    public static LocatorMapper getInstance() {
        if (instance == null) {
            instance = new LocatorMapper();
        }
        return instance;
    }
    
    public void buildIndex() {
        locatorToId.clear();
        Map<String, Map<String, String>> allLocators = repository.getAllLocators();
        
        for (Map.Entry<String, Map<String, String>> pageEntry : allLocators.entrySet()) {
            String page = pageEntry.getKey();
            for (Map.Entry<String, String> elementEntry : pageEntry.getValue().entrySet()) {
                String element = elementEntry.getKey();
                String locator = elementEntry.getValue();
                String elementId = page + "." + element;
                locatorToId.put(locator, elementId);
            }
        }
    }
    
    public String getElementId(String locator) {
        return locatorToId.get(locator);
    }
    
    public boolean isManaged(String locator) {
        return locatorToId.containsKey(locator);
    }
}

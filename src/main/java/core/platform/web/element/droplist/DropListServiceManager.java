package core.platform.web.element.droplist;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DropListServiceManager {
    private static final Map<String, DropListService> services = new ConcurrentHashMap<>();
    private static DropListServiceManager instance;

    private DropListServiceManager(){
        loadServices();
    }

    public static synchronized DropListServiceManager getInstance(){
        if(instance == null){
            instance = new DropListServiceManager();
        }
        return instance;
    }

    private void loadServices() {
        Map<String, DropListService> tempServices = new HashMap<>();
        ServiceLoader<DropListService> loader = ServiceLoader.load(DropListService.class);

        for (DropListService service : loader) {
            String type = service.getType();
            DropListService existing = tempServices.get(type);

            if (existing == null || service.getPriority() > existing.getPriority()) {
                tempServices.put(type, service);
            }
        }

        services.putAll(tempServices);
    }

    public DropListService getService(String type){
        DropListService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("No DropList service found for type: " + type);
        }
        return service;
    }
    public boolean hasService(String type) {
        return services.containsKey(type);
    }
}

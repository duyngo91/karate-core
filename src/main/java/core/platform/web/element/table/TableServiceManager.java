package core.platform.web.element.table;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class TableServiceManager {
    private static final Map<String, TableService> services = new ConcurrentHashMap<>();
    private static TableServiceManager instance;

    private TableServiceManager() {
        loadServices();
    }

    public static synchronized TableServiceManager getInstance() {
        if (instance == null) {
            instance = new TableServiceManager();
        }
        return instance;
    }

    private void loadServices() {
        Map<String, TableService> tempServices = new HashMap<>();
        ServiceLoader<TableService> loader = ServiceLoader.load(TableService.class);

        for (TableService service : loader) {
            String type = service.getType();
            TableService existing = tempServices.get(type);

            if (existing == null || service.getPriority() > existing.getPriority()) {
                tempServices.put(type, service);
            }
        }

        services.putAll(tempServices);
    }

    public TableService getService(String type) {
        TableService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("No Table service found for type: " + type);
        }
        return service;
    }
}
